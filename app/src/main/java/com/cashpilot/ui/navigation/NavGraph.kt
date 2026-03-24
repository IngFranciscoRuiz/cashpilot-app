package com.cashpilot.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cashpilot.ui.components.AdBanner
import com.cashpilot.ui.viewmodel.AdsViewModel
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cashpilot.ui.screens.add.AddExpenseScreen
import com.cashpilot.ui.screens.add.AddIncomeScreen
import com.cashpilot.ui.screens.dashboard.DashboardScreen
import com.cashpilot.ui.screens.income.IncomeListScreen
import com.cashpilot.ui.screens.expenses.ExpensesListScreen
import com.cashpilot.ui.screens.onboarding.OnboardingScreen
import com.cashpilot.ui.screens.settings.SettingsScreen
import com.cashpilot.ui.screens.splash.SplashScreen
import com.cashpilot.ui.screens.stats.StatsScreen
import com.cashpilot.ui.screens.fixed.FixedExpensesScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object IncomeList : Screen("income_list")
    data object ExpensesList : Screen("expenses_list")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
    data object AddIncome : Screen("add_income")
    data object AddExpense : Screen("add_expense")
    data object FixedExpenses : Screen("fixed_expenses")
}

data class BottomNavDestination(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun CashPilotNavHost(
    adsViewModel: AdsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val adsRemoved by adsViewModel.adsRemoved.collectAsState(initial = false)
    val bottomDestinations = CashPilotBottomDestinations

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            val showBottomBar = currentRoute in bottomDestinations.map { it.screen.route }

            if (showBottomBar) {
                Column {
                    if (!adsRemoved) {
                        AdBanner()
                    }
                    NavigationBar {
                    bottomDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.screen.route,
                            onClick = {
                                navController.navigate(dest.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onGoToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onGoToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onContinue = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddIncome = { navController.navigate(Screen.AddIncome.route) },
                    onAddVariableExpense = { navController.navigate(Screen.AddExpense.route) },
                    onOpenFixedExpenses = { navController.navigate(Screen.FixedExpenses.route) }
                )
            }
            composable(Screen.IncomeList.route) {
                IncomeListScreen(onAddIncome = { navController.navigate(Screen.AddIncome.route) })
            }
            composable(Screen.ExpensesList.route) {
                ExpensesListScreen(onAddExpense = { navController.navigate(Screen.AddExpense.route) })
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.AddIncome.route) {
                AddIncomeScreen(
                    onDone = { navController.popBackStack() }
                )
            }
            composable(Screen.AddExpense.route) {
                AddExpenseScreen(
                    onDone = { navController.popBackStack() }
                )
            }
            composable(Screen.FixedExpenses.route) {
                FixedExpensesScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

