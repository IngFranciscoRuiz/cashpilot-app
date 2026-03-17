package com.cashpilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cashpilot.data.local.entity.IncomeEntity
import com.cashpilot.data.local.entity.PeriodType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Query("SELECT * FROM income WHERE periodType = :periodType AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomeForPeriod(
        periodType: PeriodType,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<IncomeEntity>>

    @Query("SELECT SUM(amount) FROM income WHERE periodType = :periodType AND date BETWEEN :start AND :end")
    fun getTotalIncomeForPeriod(
        periodType: PeriodType,
        start: LocalDate,
        end: LocalDate
    ): Flow<Double?>

    @Query("DELETE FROM income")
    suspend fun deleteAll()
}

