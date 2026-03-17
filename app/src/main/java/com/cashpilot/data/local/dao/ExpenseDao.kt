package com.cashpilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cashpilot.data.local.entity.ExpenseEntity
import com.cashpilot.data.local.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expense WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesForRange(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expense WHERE date BETWEEN :start AND :end")
    fun getTotalExpensesForRange(
        start: LocalDate,
        end: LocalDate
    ): Flow<Double?>

    @Query("SELECT category, SUM(amount) as total FROM expense WHERE date BETWEEN :start AND :end GROUP BY category")
    fun getTotalsByCategory(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<CategoryTotal>>

    data class CategoryTotal(
        val category: ExpenseCategory,
        val total: Double
    )

    @Query("DELETE FROM expense")
    suspend fun deleteAll()
}

