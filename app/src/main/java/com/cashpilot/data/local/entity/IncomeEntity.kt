package com.cashpilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val amount: Double,
    val date: LocalDate,
    val periodType: PeriodType
)

enum class PeriodType {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}

