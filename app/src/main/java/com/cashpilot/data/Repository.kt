package com.cashpilot.data

import com.cashpilot.data.local.dao.ExpenseDao
import com.cashpilot.data.local.dao.FixedExpenseDao
import com.cashpilot.data.local.dao.IncomeDao
import com.cashpilot.data.local.entity.ExpenseCategory
import com.cashpilot.data.local.entity.ExpenseEntity
import com.cashpilot.data.local.entity.FixedExpenseEntity
import com.cashpilot.data.local.entity.IncomeEntity
import com.cashpilot.data.local.entity.PeriodType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashPilotRepository @Inject constructor(
    private val incomeDao: IncomeDao,
    private val fixedExpenseDao: FixedExpenseDao,
    private val expenseDao: ExpenseDao
) {

    suspend fun addIncome(
        name: String,
        amount: Double,
        date: LocalDate,
        periodType: PeriodType
    ) {
        incomeDao.insertIncome(
            IncomeEntity(
                name = name,
                amount = amount,
                date = date,
                periodType = periodType
            )
        )
    }

    fun getIncomeForPeriod(
        periodType: PeriodType,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<IncomeEntity>> = incomeDao.getIncomeForPeriod(periodType, start, end)

    fun getTotalIncomeForPeriod(
        periodType: PeriodType,
        start: LocalDate,
        end: LocalDate
    ): Flow<Double?> = incomeDao.getTotalIncomeForPeriod(periodType, start, end)

    suspend fun addFixedExpense(
        name: String,
        amount: Double,
        category: String,
        periodType: PeriodType
    ) {
        fixedExpenseDao.insertFixedExpense(
            FixedExpenseEntity(
                name = name,
                amount = amount,
                category = category,
                periodType = periodType
            )
        )
    }

    fun getFixedExpensesForPeriod(periodType: PeriodType): Flow<List<FixedExpenseEntity>> =
        fixedExpenseDao.getFixedExpensesForPeriod(periodType)

    fun getTotalFixedForPeriod(periodType: PeriodType): Flow<Double?> =
        fixedExpenseDao.getTotalFixedForPeriod(periodType)

    suspend fun addVariableExpense(
        name: String,
        amount: Double,
        category: ExpenseCategory,
        date: LocalDate,
        notes: String?,
        sortOrderMillis: Long = System.currentTimeMillis()
    ) {
        expenseDao.insertExpense(
            ExpenseEntity(
                name = name,
                amount = amount,
                category = category,
                date = date,
                notes = notes,
                sortOrderMillis = sortOrderMillis
            )
        )
    }

    fun getTotalIncomeInDateRange(start: LocalDate, end: LocalDate): Flow<Double?> =
        incomeDao.getTotalIncomeInDateRange(start, end)

    fun getVariableExpensesForRange(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<ExpenseEntity>> = expenseDao.getExpensesForRange(start, end)

    fun getTotalVariableForRange(
        start: LocalDate,
        end: LocalDate
    ): Flow<Double?> = expenseDao.getTotalExpensesForRange(start, end)

    fun getTotalsByCategory(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<ExpenseDao.CategoryTotal>> = expenseDao.getTotalsByCategory(start, end)

    /** Borra todos los ingresos, gastos fijos y gastos variables. */
    suspend fun clearAllData() {
        incomeDao.deleteAll()
        fixedExpenseDao.deleteAll()
        expenseDao.deleteAll()
    }

    /** Nombres de gastos (fijos + variables) para sugerencias al registrar. Últimos 12 meses variables. */
    fun getExpenseNamesForSuggestions(periodType: PeriodType): Flow<List<String>> {
        val today = LocalDate.now()
        val rangeStart = today.minusMonths(12)
        return combine(
            getFixedExpensesForPeriod(periodType),
            getVariableExpensesForRange(rangeStart, today)
        ) { fixedList, variableList ->
            (fixedList.map { it.name } + variableList.map { it.name })
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
                .take(25)
        }
    }
}

