package com.cashpilot.di

import android.content.Context
import androidx.room.Room
import com.cashpilot.data.CashPilotRepository
import com.cashpilot.data.local.CashPilotDatabase
import com.cashpilot.data.local.dao.ExpenseDao
import com.cashpilot.data.local.dao.FixedExpenseDao
import com.cashpilot.data.local.dao.IncomeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CashPilotDatabase =
        Room.databaseBuilder(
            context,
            CashPilotDatabase::class.java,
            "cashpilot-db"
        ).build()

    @Provides
    fun provideIncomeDao(db: CashPilotDatabase): IncomeDao = db.incomeDao()

    @Provides
    fun provideFixedExpenseDao(db: CashPilotDatabase): FixedExpenseDao = db.fixedExpenseDao()

    @Provides
    fun provideExpenseDao(db: CashPilotDatabase): ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideRepository(
        incomeDao: IncomeDao,
        fixedExpenseDao: FixedExpenseDao,
        expenseDao: ExpenseDao
    ): CashPilotRepository = CashPilotRepository(incomeDao, fixedExpenseDao, expenseDao)
}

