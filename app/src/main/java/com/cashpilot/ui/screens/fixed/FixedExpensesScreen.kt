package com.cashpilot.ui.screens.fixed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashpilot.ui.components.MoneyAmountOutlinedField
import com.cashpilot.ui.util.formatPesosMx
import com.cashpilot.ui.util.parsePositiveMoneyOrNull
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FixedExpensesScreen(
    onBack: () -> Unit,
    viewModel: FixedExpensesViewModel = hiltViewModel()
) {
    val items by viewModel.fixedExpenses.collectAsState()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rent") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Gastos fijos",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorMessage = null },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )
        Spacer(modifier = Modifier.height(8.dp))
        MoneyAmountOutlinedField(
            value = amount,
            onValueChange = { amount = it; errorMessage = null },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )
        errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                errorMessage = null
                val amt = parsePositiveMoneyOrNull(amount)
                val nombre = name.trim()
                when {
                    nombre.isBlank() -> errorMessage = "Escribe un nombre"
                    amt == null -> errorMessage = "Ingresa un monto mayor que 0"
                    else -> {
                        viewModel.addFixedExpense(
                            name = nombre,
                            amount = amt,
                            category = category
                        )
                        name = ""
                        amount = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Guardar gasto fijo")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(items) { item ->
                Text(
                    text = "${item.name}: ${formatPesosMx(item.amount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

