package com.cashpilot.util

import com.cashpilot.data.local.entity.PeriodType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Primer y último día del mes calendario actual (ingresos y gastos variables). */
fun currentCalendarMonthRange(today: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate> {
    val start = today.withDayOfMonth(1)
    val end = today.withDayOfMonth(today.lengthOfMonth())
    return start to end
}

fun currentPeriodRange(periodType: PeriodType): Pair<LocalDate, LocalDate> {
    val today = LocalDate.now()
    return when (periodType) {
        PeriodType.WEEKLY -> {
            val start = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            val end = start.plusDays(6)
            start to end
        }
        PeriodType.BIWEEKLY -> {
            if (today.dayOfMonth <= 15) {
                today.withDayOfMonth(1) to today.withDayOfMonth(15)
            } else {
                today.withDayOfMonth(16) to today.withDayOfMonth(today.lengthOfMonth())
            }
        }
        PeriodType.MONTHLY -> {
            today.withDayOfMonth(1) to today.withDayOfMonth(today.lengthOfMonth())
        }
    }
}

private val monthFormat = DateTimeFormatter.ofPattern("MMMM", Locale("es"))

fun periodLabel(periodType: PeriodType): String {
    val range = currentPeriodRange(periodType)
    val start = range.first
    val end = range.second
    return when (periodType) {
        PeriodType.WEEKLY -> "Semana ${start.dayOfMonth} - ${end.dayOfMonth} ${start.format(monthFormat)}"
        PeriodType.BIWEEKLY -> {
            val quincena = if (start.dayOfMonth == 1) "1" else "2"
            "Quincena $quincena - ${end.dayOfMonth} ${end.format(monthFormat)}"
        }
        PeriodType.MONTHLY -> "${start.format(monthFormat)}"
    }
}
