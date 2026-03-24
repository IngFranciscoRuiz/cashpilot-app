package com.cashpilot.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.ui.util.formatPesosMx
import com.cashpilot.util.formatSpanishMonthYear
import java.time.LocalDate

@Composable
fun ExpensesListScreen(
    onAddExpense: () -> Unit,
    viewModel: ExpensesListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val monthTitle = formatSpanishMonthYear(LocalDate.now())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Gastos",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Text(
                text = "Gastos fijos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (state.fixedExpenses.isEmpty()) {
            item {
                Text(
                    text = "Sin gastos fijos. Añádelos desde Inicio → tocar tarjeta Gastos Fijos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            items(
                count = state.fixedExpenses.size,
                key = { idx -> "fixed_${state.fixedExpenses[idx].id}" }
            ) { i ->
                val item = state.fixedExpenses[i]
                FixedExpenseRow(name = item.name, amount = item.amount, category = item.category)
            }
            item {
                Text(
                    text = "Total gastos fijos: ${formatPesosMx(state.totalFixed)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Gastos variables · $monthTitle",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.variableDayGroups.isEmpty()) {
            item {
                Text(
                    text = "Sin gastos este mes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            state.variableDayGroups.forEach { group ->
                item(key = "hdr_${group.keyDate}") {
                    DaySectionHeader(label = group.headerLabel, dayTotal = group.dayTotal)
                }
                items(
                    count = group.expenses.size,
                    key = { idx -> "var_${group.keyDate}_${group.expenses[idx].id}" }
                ) { i ->
                    val exp = group.expenses[i]
                    ExpenseRow(name = exp.name, amount = exp.amount)
                }
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Total del mes: ${formatPesosMx(state.totalVariableMonth)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Te quedan",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Text(
                text = "${formatPesosMx(state.remaining)} disponibles",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddExpense,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Registrar gasto")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DaySectionHeader(label: String, dayTotal: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatPesosMx(dayTotal),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun FixedExpenseRow(name: String, amount: Double, category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${formatPesosMx(amount)} · $category",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ExpenseRow(name: String, amount: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatPesosMx(amount),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}
