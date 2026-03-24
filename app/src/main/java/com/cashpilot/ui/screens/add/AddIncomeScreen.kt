package com.cashpilot.ui.screens.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.ui.components.MoneyAmountOutlinedField
import com.cashpilot.ui.screens.income.IncomeViewModel
import com.cashpilot.ui.util.parsePositiveMoneyOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    onDone: () -> Unit,
    viewModel: IncomeViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("Salario") }
    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Añadir ingreso",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        MoneyAmountOutlinedField(
            value = amount,
            onValueChange = { amount = it; amountError = null },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth(),
            isError = amountError != null
        )
        amountError?.let { msg ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val parsed = parsePositiveMoneyOrNull(amount)
                if (parsed == null) {
                    amountError = "Ingresa un monto mayor que 0"
                    return@Button
                }
                viewModel.addIncome(name, parsed)
                onDone()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Guardar ingreso")
        }
    }
}
