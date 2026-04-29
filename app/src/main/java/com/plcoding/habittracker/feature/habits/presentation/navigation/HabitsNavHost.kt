package com.plcoding.habittracker.feature.habits.presentation.navigation

import PreviewScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.plcoding.habittracker.feature.habits.presentation.createedit.CreateEditHabitScreenRoot
import com.plcoding.habittracker.feature.habits.presentation.statistics.StatisticsScreenRoot
import com.plcoding.habittracker.feature.habits.presentation.today.TodayScreenRoot

@Composable
fun HabitsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        // Use the Type-Safe object directly, not a string route
        startDestination = Route.Preview,
        modifier = modifier,
    ) {
        composable<Route.Preview> {
            PreviewScreen(
                navController = navController,
                onAnimationFinished = {
                    navController.navigate(Route.Today)
                }
            )
        }

        composable<Route.Today> {
            TodayScreenRoot(
                onNavigateToEdit = { habitId ->
                    navController.navigate(Route.CreateEditHabit(habitId = habitId))
                },
                onNavigateToCreate = {
                    navController.navigate(Route.CreateEditHabit( habitId = -1))
                },
                onNavigateToStats = {
                    navController.navigate(Route.Statistics)
                },
            )
        }

        composable<Route.Statistics> {
            StatisticsScreenRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<Route.CreateEditHabit> {
            CreateEditHabitScreenRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}


