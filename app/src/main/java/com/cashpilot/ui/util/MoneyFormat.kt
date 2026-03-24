package com.cashpilot.ui.util

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

private val localeMx: Locale = Locale.forLanguageTag("es-MX")

/** Cantidad en pesos mexicanos (ej. $1,234.56). Nueva instancia por llamada (seguro entre hilos). */
fun formatPesosMx(amount: Double, decimals: Boolean = true): String {
    val nf = NumberFormat.getCurrencyInstance(localeMx)
    if (decimals) {
        nf.maximumFractionDigits = 2
        nf.minimumFractionDigits = 2
    } else {
        nf.maximumFractionDigits = 0
        nf.minimumFractionDigits = 0
    }
    nf.roundingMode = RoundingMode.HALF_UP
    return nf.format(amount)
}
