package com.cashpilot.ui.screens.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.billing.BillingManager
import com.cashpilot.data.local.entity.PeriodType

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val periodType by viewModel.periodType.collectAsState()
    var pendingPeriodType by remember { mutableStateOf<PeriodType?>(null) }
    val adsRemoved by viewModel.adsRemoved.collectAsState(initial = false)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.purchaseResult.collect { result ->
            when (result) {
                is BillingManager.PurchaseResult.Success -> {
                    snackbarHostState.showSnackbar("Anuncios desactivados. Gracias por tu compra.")
                }
                is BillingManager.PurchaseResult.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Periodo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Los ingresos y gastos fijos se asocian al periodo elegido. Si cambias el periodo, se reiniciarán todos tus datos (ingresos, gastos fijos y variables).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                PeriodType.WEEKLY to "Semanal",
                PeriodType.BIWEEKLY to "Quincenal",
                PeriodType.MONTHLY to "Mensual"
            ).forEach { (type, label) ->
                FilterChip(
                    selected = periodType == type,
                    onClick = {
                        if (type != periodType) {
                            pendingPeriodType = type
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (periodType == type) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        pendingPeriodType?.let { newType ->
            AlertDialog(
                onDismissRequest = { pendingPeriodType = null },
                title = { Text("Cambiar periodo") },
                text = {
                    Text("Si cambias el periodo se reiniciarán tus datos (ingresos, gastos fijos y variables). ¿Deseas continuar?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setPeriodTypeAndClearData(newType)
                            pendingPeriodType = null
                        }
                    ) {
                        Text("Sí, reiniciar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingPeriodType = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Anuncios",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Puedes quitar los anuncios de la app con una compra única.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (adsRemoved) {
            Text(
                text = "Anuncios desactivados",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(
                onClick = {
                    activity?.let { viewModel.launchRemoveAdsPurchase(it) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quitar anuncios")
            }
        }
    }
    }
}

