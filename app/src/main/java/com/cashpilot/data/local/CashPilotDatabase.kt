package com.cashpilot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cashpilot.data.local.dao.ExpenseDao
import com.cashpilot.data.local.dao.FixedExpenseDao
import com.cashpilot.data.local.dao.IncomeDao
import com.cashpilot.data.local.entity.ExpenseEntity
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.local.entity.IncomeEntity

@Database(
    entities = [
        IncomeEntity::class,
        FixedExpenseEntity::class,
        ExpenseEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CashPilotDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun fixedExpenseDao(): FixedExpenseDao
    abstract fun expenseDao(): ExpenseDao
}

