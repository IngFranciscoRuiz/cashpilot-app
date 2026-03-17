package com.cashpilot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PieChart

val CashPilotBottomDestinations = listOf(
    BottomNavDestination(
        screen = Screen.Dashboard,
        label = "Inicio",
        icon = Icons.Filled.Home
    ),
    BottomNavDestination(
        screen = Screen.IncomeList,
        label = "Ingresos",
        icon = Icons.Filled.AccountBalance
    ),
    BottomNavDestination(
        screen = Screen.ExpensesList,
        label = "Gastos",
        icon = Icons.Filled.Receipt
    ),
    BottomNavDestination(
        screen = Screen.Stats,
        label = "Resumen",
        icon = Icons.Filled.PieChart
    ),
    BottomNavDestination(
        screen = Screen.Settings,
        label = "Ajustes",
        icon = Icons.Filled.Settings
    )
)
