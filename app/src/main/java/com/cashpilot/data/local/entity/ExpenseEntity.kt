package com.cashpilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: LocalDate,
    val notes: String?
)

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    SHOPPING,
    ENTERTAINMENT,
    BILLS,
    OTHER
}

