package com.cashpilot.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.ui.util.formatPesosMx

private val chartColors = listOf(
    Color(0xFF2E7D32),
    Color(0xFFE65100),
    Color(0xFFC62828),
    Color(0xFF1E88E5),
    Color(0xFF0288D1),
    Color(0xFFF9A825)
)

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val totalGastos = state.gastosFijos + state.gastosVariables
    val progressGastos = if (state.dineroDelMes > 0) {
        (totalGastos / state.dineroDelMes).toFloat().coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // —— Header: Resumen + periodo ——
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            if (state.periodLabel.isNotBlank()) {
                Text(
                    text = state.periodLabel.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // —— Hero: Saldo del periodo ——
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (state.dineroRestante >= 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Saldo del periodo",
                    style = MaterialTheme.typography.titleMedium,
                    color = (if (state.dineroRestante >= 0)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onErrorContainer).copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatPesosMx(state.dineroRestante),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (state.dineroRestante >= 0)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ingresos − Gastos = Restante",
                    style = MaterialTheme.typography.bodySmall,
                    color = (if (state.dineroRestante >= 0)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onErrorContainer).copy(alpha = 0.75f)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // —— Barra: Ingresos vs Gastos ——
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gastos vs Ingresos",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    text = "${formatPesosMx(state.dineroDelMes, decimals = false)}  /  -${formatPesosMx(totalGastos, decimals = false)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progressGastos },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = MaterialTheme.colorScheme.error,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // —— Resumen del periodo (filas con indicador visual) ——
        Text(
            text = "Resumen del periodo",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRowWithDot("Ingresos", state.dineroDelMes, MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRowWithDot("Gastos fijos", state.gastosFijos, MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRowWithDot("Gastos variables", state.gastosVariables, MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

        // —— ¿En qué te fuiste el dinero? (por nombre: fijos + variables) ——
        Text(
            text = "¿En qué se te va el dinero?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Por nombre (gastos fijos y variables)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.expenseByNameTotals.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChartWithCenter(
                        values = state.expenseByNameTotals.map { it.second.toFloat() },
                        colors = chartColors,
                        centerLabel = formatPesosMx(state.expenseByNameTotals.sumOf { it.second }, decimals = false)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    val totalAll = state.expenseByNameTotals.sumOf { it.second }
                    val maxAmount = state.expenseByNameTotals.maxOfOrNull { it.second } ?: 1.0
                    state.expenseByNameTotals.sortedByDescending { it.second }.forEachIndexed { index, (name, amount) ->
                        val pct = if (totalAll > 0) (amount / totalAll).toFloat() else 0f
                        val isTop = index == 0
                        val displayName = name.ifBlank { "Sin nombre" }.replaceFirstChar { it.uppercase() }
                        LegendRowWithBar(
                            color = chartColors.getOrElse(index) { Color.Gray },
                            label = displayName,
                            amount = amount,
                            percent = (pct * 100).toInt(),
                            barProgress = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f,
                            isHighlight = isTop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sin gastos registrados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Registra gastos fijos y variables para ver el desglose por nombre aquí.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

        // —— Fijos vs Variables ——
        if (totalGastos > 0) {
            Text(
                text = "Fijos vs Variables",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // weight() exige > 0; si solo hay fijos o solo variables, acotamos para no pasar 0
                    val fixedRatio = if (totalGastos > 0) (state.gastosFijos / totalGastos).toFloat().coerceIn(0.01f, 0.99f) else 0.5f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(fixedRatio)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f - fixedRatio)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fijos ${formatPesosMx(state.gastosFijos, decimals = false)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Variables ${formatPesosMx(state.gastosVariables, decimals = false)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // —— Insights (tarjetas separadas) ——
        Text(
            text = "Insights",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.insight.isNotBlank()) {
            InsightCard(
                text = state.insight,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (state.insightSecondary.isNotBlank()) {
            InsightCard(
                text = state.insightSecondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (state.insight.isBlank() && state.insightSecondary.isBlank()) {
            InsightCard(
                text = "Añade ingresos y gastos para recibir recomendaciones y ver comparativas.",
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SummaryRowWithDot(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = formatPesosMx(amount),
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LegendRowWithBar(
    color: Color,
    label: String,
    amount: Double,
    percent: Int,
    barProgress: Float,
    isHighlight: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(12.dp)
                        .height(12.dp)
                        .background(color, RoundedCornerShape(3.dp))
                )
                Text(
                    text = label.ifBlank { "—" }.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
                )
            }
            Text(
                text = "${formatPesosMx(amount, decimals = false)} ($percent%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { barProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun DonutChartWithCenter(
    values: List<Float>,
    colors: List<Color>,
    centerLabel: String
) {
    val safeValues = values.filter { it.isFinite() && it > 0f }
    if (safeValues.isEmpty()) return
    val total = safeValues.sum()
    if (total <= 0f) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val strokeWidth = 36.dp.toPx()
            val radius = (size.minDimension / 2f) - strokeWidth
            val center = Offset(size.width / 2f, size.height / 2f)
            var startAngle = -90f

            safeValues.forEachIndexed { index, value ->
                val sweep = 360f * (value / total)
                val color = colors.getOrElse(index) { Color.Gray }
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }
        Text(
            text = centerLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InsightCard(
    text: String,
    containerColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(16.dp)
        )
    }
}
