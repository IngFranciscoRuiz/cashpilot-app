package com.cashpilot.data.local

import androidx.room.TypeConverter
import com.cashpilot.data.local.entity.ExpenseCategory
import com.cashpilot.data.local.entity.PeriodType
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? {
        if (value == null) return null
        return try {
            // Nuevos datos: guardados como epoch day (número)
            value.toLongOrNull()?.let { LocalDate.ofEpochDay(it) }
                ?: LocalDate.parse(value) // Fallback: formato ISO "yyyy-MM-dd"
        } catch (_: Exception) {
            null
        }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? =
        date?.toEpochDay()?.toString()

    @TypeConverter
    fun fromPeriodType(value: String?): PeriodType? =
        value?.let { PeriodType.valueOf(it) }

    @TypeConverter
    fun periodTypeToString(type: PeriodType?): String? =
        type?.name

    @TypeConverter
    fun fromExpenseCategory(value: String?): ExpenseCategory? =
        value?.let { ExpenseCategory.valueOf(it) }

    @TypeConverter
    fun expenseCategoryToString(category: ExpenseCategory?): String? =
        category?.name
}

