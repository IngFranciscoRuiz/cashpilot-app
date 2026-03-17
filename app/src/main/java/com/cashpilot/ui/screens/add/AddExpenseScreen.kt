package com.cashpilot.ui.screens.add

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.data.local.entity.ExpenseCategory
import com.cashpilot.ui.screens.expenses.ExpensesViewModel
import kotlinx.coroutines.launch

private fun ExpenseCategory.displayName(): String = when (this) {
    ExpenseCategory.FOOD -> "Comida"
    ExpenseCategory.TRANSPORT -> "Transporte"
    ExpenseCategory.SHOPPING -> "Compras"
    ExpenseCategory.ENTERTAINMENT -> "Entretenimiento"
    ExpenseCategory.BILLS -> "Facturas"
    ExpenseCategory.OTHER -> "Otro"
}

@Composable
fun AddExpenseScreen(
    onDone: () -> Unit,
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    var quickInput by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val nameSuggestions by viewModel.expenseNameSuggestions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Añadir gasto",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorMessage = null },
            label = { Text("Descripción del gasto") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' }; errorMessage = null },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Opcional: entrada rápida (ej: 60 café) para rellenar monto y descripción a la vez.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = quickInput,
            onValueChange = { quickInput = it; errorMessage = null },
            label = { Text("Entrada rápida") },
            modifier = Modifier.fillMaxWidth()
        )
        if (nameSuggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usar nombre anterior (agrupa mejor):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                nameSuggestions.take(12).forEach { suggestion ->
                    SuggestionChip(
                        onClick = { name = suggestion },
                        label = { Text(suggestion, maxLines = 1) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Categoría",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExpenseCategory.entries.chunked(3).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCategories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.displayName(), maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    errorMessage = null
                    // Descripción: prioridad al campo "Descripción"; solo usar entrada rápida si está vacío
                    val desc = name.trim().let { n ->
                        if (n.isNotBlank()) n
                        else if (quickInput.isNotBlank()) {
                            quickInput.trim().split(" ").filter { it.isNotBlank() }.drop(1).joinToString(" ").ifBlank { "Gasto" }
                        } else "Gasto"
                    }
                    // Monto: prioridad al campo "Monto"; si no, extraer de entrada rápida
                    var amt = amount.trim().replace(",", ".").toDoubleOrNull()
                    if (amt == null && quickInput.isNotBlank()) {
                        val firstPart = quickInput.trim().split(" ").filter { it.isNotBlank() }.firstOrNull()
                        amt = firstPart?.filter { it.isDigit() || it == '.' }?.toDoubleOrNull()
                    }
                    if (amt == null || amt <= 0) {
                        errorMessage = "Escribe un monto válido mayor que 0"
                        return@launch
                    }
                    try {
                        viewModel.saveVariableExpense(
                            name = desc,
                            amount = amt,
                            category = selectedCategory
                        )
                        onDone()
                    } catch (e: Exception) {
                        errorMessage = "No se pudo guardar: ${e.message ?: "error desconocido"}"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Guardar gasto")
        }
    }
}

