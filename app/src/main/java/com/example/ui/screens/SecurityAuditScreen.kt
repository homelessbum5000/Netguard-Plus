package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.viewmodels.NetworkFirewallViewModel

@Composable
fun SecurityAuditScreen(
    viewModel: NetworkFirewallViewModel,
    modifier: Modifier = Modifier
) {
    HardwareAccessScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}
