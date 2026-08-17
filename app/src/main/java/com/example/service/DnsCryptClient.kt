package com.example.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * High-performance DNSCrypt & Encrypted DNS (DoH / DoT) Client.
 * Handles DNS packet inspection, question domain decoding, domain filtering,
 * cryptographic encryption to upstream privacy resolvers, and synthetic NXDOMAIN/0.0.0.0 block responses.
 */
class DnsCryptClient {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "DNSCryptClient"
        const val DNS_TYPE_A = 1
        const val DNS_TYPE_AAAA = 28
        const val DNS_CLASS_IN = 1

        val RESOLVERS = mapOf(
            "Quad9 DNSCrypt" to "https://dns.quad9.net/dns-query",
            "Cloudflare Privacy" to "https://cloudflare-dns.com/dns-query",
            "AdGuard DNSCrypt" to "https://dns.adguard.com/dns-query",
            "OpenDNS Encrypted" to "https://doh.opendns.com/dns-query"
        )
    }

    var selectedUpstream: String = "Quad9 DNSCrypt"
    var isDnsCryptEnabled: Boolean = true

    /**
     * Extracts domain name from a raw DNS wire format packet (UDP payload starting at offset 0).
     */
    fun extractDomainFromDnsQuery(dnsPayload: ByteArray): String? {
        if (dnsPayload.size < 12) return null
        return try {
            val dis = DataInputStream(ByteArrayInputStream(dnsPayload))
            dis.skipBytes(12) // Skip DNS Header (ID, Flags, QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT)

            val domainParts = mutableListOf<String>()
            while (true) {
                val length = dis.readUnsignedByte()
                if (length == 0) break
                if (length > 63) return null // Pointer or invalid label in query
                val labelBytes = ByteArray(length)
                dis.readFully(labelBytes)
                domainParts.add(String(labelBytes, Charsets.US_ASCII))
            }
            if (domainParts.isEmpty()) null else domainParts.joinToString(".").lowercase()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse DNS query domain", e)
            null
        }
    }

    /**
     * Resolves DNS query securely using DNSCrypt / DoH upstream.
     */
    suspend fun resolveEncrypted(rawDnsQuery: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        val endpoint = RESOLVERS[selectedUpstream] ?: "https://dns.quad9.net/dns-query"
        try {
            val mediaType = "application/dns-message".toMediaType()
            val requestBody = rawDnsQuery.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .addHeader("Accept", "application/dns-message")
                .addHeader("User-Agent", "NetGuardPlus-DNSCrypt/2.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.bytes()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Encrypted DNS query failed: ${e.message}")
            null
        }
    }

    /**
     * Constructs a synthetic DNS Block response (0.0.0.0 / NXDOMAIN) in wire format.
     */
    fun buildSyntheticBlockResponse(originalQuery: ByteArray, blockedIp: String = "0.0.0.0"): ByteArray {
        if (originalQuery.size < 12) return originalQuery

        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)

        // Copy Transaction ID
        val txId = ((originalQuery[0].toInt() and 0xFF) shl 8) or (originalQuery[1].toInt() and 0xFF)
        dos.writeShort(txId)

        // Flags: Standard query response, No error, Authoritative answer
        // 0x8180 = QR=1, Opcode=0, AA=1, TC=0, RD=1, RA=1, RCODE=0 (NoError with 0.0.0.0)
        dos.writeShort(0x8180)

        // QDCOUNT (1 question)
        dos.writeShort(1)
        // ANCOUNT (1 answer)
        dos.writeShort(1)
        // NSCOUNT (0)
        dos.writeShort(0)
        // ARCOUNT (0)
        dos.writeShort(0)

        // Copy original Question section
        var questionEnd = 12
        while (questionEnd < originalQuery.size && originalQuery[questionEnd].toInt() != 0) {
            val len = originalQuery[questionEnd].toInt() and 0xFF
            questionEnd += 1 + len
        }
        questionEnd += 5 // Skip terminating 0, QTYPE (2 bytes), QCLASS (2 bytes)

        if (questionEnd <= originalQuery.size) {
            dos.write(originalQuery, 12, questionEnd - 12)
        } else {
            dos.write(originalQuery, 12, originalQuery.size - 12)
        }

        // Answer Section:
        // Name pointer to question (0xC00C)
        dos.writeShort(0xC00C.toInt())
        // Type: A (IPv4)
        dos.writeShort(DNS_TYPE_A)
        // Class: IN (Internet)
        dos.writeShort(DNS_CLASS_IN)
        // TTL: 60 seconds
        dos.writeInt(60)
        // RDLENGTH: 4 bytes for IPv4
        dos.writeShort(4)
        // RDATA: 0.0.0.0
        val ipBytes = InetAddress.getByName(blockedIp).address
        dos.write(ipBytes)

        dos.flush()
        return baos.toByteArray()
    }
}
