package com.cashpilot.util

import java.text.Normalizer

/**
 * Normaliza el nombre de un gasto para agrupar: "Café del OXXO" y "cafe del oxxo" quedan igual.
 * - Trim, minúsculas, espacios múltiples → uno solo.
 * - Opcional: quitar acentos (café → cafe) para agrupar más.
 */
fun normalizeExpenseNameForGrouping(name: String): String {
    if (name.isBlank()) return ""
    val trimmed = name.trim()
    val singleSpaces = trimmed.replace(Regex("\\s+"), " ")
    val lower = singleSpaces.lowercase()
    val withoutAccents = Normalizer.normalize(lower, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "") // quita acentos
    return withoutAccents
}

/**
 * Agrupa gastos por nombre normalizado. "Café del Oxxo" y "CAFÉ DEL OXXO" se suman en uno.
 * Devuelve lista (nombreParaMostrar, total) ordenada por total descendente.
 * Para mostrar se usa el nombre que más aportó en ese grupo (el de mayor monto individual).
 */
fun groupExpensesByName(entries: List<Pair<String, Double>>): List<Pair<String, Double>> {
    if (entries.isEmpty()) return emptyList()
    val grouped = entries
        .filter { it.first.isNotBlank() }
        .groupBy({ normalizeExpenseNameForGrouping(it.first) }, { it.first to it.second })
        .mapValues { (_, list) ->
            val total = list.sumOf { it.second }
            val displayName = list.maxByOrNull { it.second }?.first?.takeIf { it.isNotBlank() } ?: "Sin nombre"
            displayName to total
        }
    return grouped.values.toList().sortedByDescending { it.second }
}
