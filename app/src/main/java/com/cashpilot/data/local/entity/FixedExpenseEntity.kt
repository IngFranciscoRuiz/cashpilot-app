package com.cashpilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixed_expense")
data class FixedExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val amount: Double,
    val category: String,
    val isRecurring: Boolean = true,
    val periodType: PeriodType
)

