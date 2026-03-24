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
    val notes: String?,
    /** Para ordenar dentro del mismo día (más reciente primero). Migración: id * 1000 en datos viejos. */
    val sortOrderMillis: Long = 0L
)

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    SHOPPING,
    ENTERTAINMENT,
    BILLS,
    OTHER
}

