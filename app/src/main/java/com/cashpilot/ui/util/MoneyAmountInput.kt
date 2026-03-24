package com.cashpilot.ui.util

private const val MONEY_INPUT_MAX_CHARS = 14

/**
 * Solo dígitos y un punto decimal. Coma → punto.
 * Limita longitud para evitar lag; no reformatea miles mientras escribes (mejor respuesta del IME).
 */
fun sanitizeMoneyAmountInput(raw: String): String {
    if (raw.isEmpty()) return ""
    val normalized = raw.replace(',', '.')
    val out = StringBuilder(MONEY_INPUT_MAX_CHARS)
    var dotSeen = false
    for (ch in normalized) {
        if (out.length >= MONEY_INPUT_MAX_CHARS) break
        when {
            ch in '0'..'9' -> out.append(ch)
            ch == '.' && !dotSeen -> {
                out.append('.')
                dotSeen = true
            }
        }
    }
    return out.toString()
}

fun parseMoneyAmountOrNull(text: String): Double? {
    val t = text.trim()
    if (t.isEmpty()) return null
    val s = sanitizeMoneyAmountInput(t)
    if (s.isEmpty() || s == ".") return null
    return s.toDoubleOrNull()
}

/** Monto válido para guardar: finito y > 0. */
fun parsePositiveMoneyOrNull(text: String): Double? {
    val v = parseMoneyAmountOrNull(text) ?: return null
    if (!v.isFinite() || v <= 0) return null
    return v
}
