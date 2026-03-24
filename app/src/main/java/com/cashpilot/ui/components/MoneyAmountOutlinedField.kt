package com.cashpilot.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.cashpilot.ui.util.sanitizeMoneyAmountInput

/**
 * Monto en pesos: prefijo $, teclado decimal, sin autocorrección (evita teclas lentas / el 0 “pegado”).
 * Solo actualiza el estado si el texto sanitizado cambia (menos recomposiciones por el IME).
 */
@Composable
fun MoneyAmountOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val sanitized = sanitizeMoneyAmountInput(raw)
            if (sanitized != value) onValueChange(sanitized)
        },
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        label = label,
        prefix = {
            Text(
                text = "$",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        suffix = {
            Text(
                text = "MXN",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            autoCorrect = false
        )
    )
}
