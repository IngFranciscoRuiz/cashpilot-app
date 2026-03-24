package com.cashpilot.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayMonthYearEs: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM uuuu", Locale("es", "ES"))

private val monthYearLongEs: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMMM uuuu", Locale("es", "ES"))

/** Ej.: "24 mar. 2026" */
fun formatSpanishDayMonthYear(date: LocalDate): String = date.format(dayMonthYearEs)

/** Ej.: "Marzo 2026" (para encabezados de mes). */
fun formatSpanishMonthYear(date: LocalDate): String =
    date.format(monthYearLongEs).replaceFirstChar { it.titlecase(Locale.getDefault()) }

fun expenseDaySectionTitle(date: LocalDate, today: LocalDate = LocalDate.now()): String =
    when (date) {
        today -> "Hoy"
        today.minusDays(1) -> "Ayer"
        else -> formatSpanishDayMonthYear(date)
    }
