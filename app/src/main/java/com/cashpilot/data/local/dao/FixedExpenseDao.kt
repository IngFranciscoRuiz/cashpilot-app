package com.cashpilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.local.entity.PeriodType
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixedExpense(expense: FixedExpenseEntity)

    @Query("SELECT * FROM fixed_expense WHERE periodType = :periodType")
    fun getFixedExpensesForPeriod(periodType: PeriodType): Flow<List<FixedExpenseEntity>>

    @Query("SELECT SUM(amount) FROM fixed_expense WHERE periodType = :periodType")
    fun getTotalFixedForPeriod(periodType: PeriodType): Flow<Double?>

    @Query("DELETE FROM fixed_expense")
    suspend fun deleteAll()
}

