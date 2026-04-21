//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.plcoding.habittracker.feature.habits.presentation.createedit.CreateEditHabitScreenRoot
//import com.plcoding.habittracker.feature.habits.presentation.statistics.StatisticsScreenRoot
//import com.plcoding.habittracker.feature.habits.presentation.today.TodayScreenRoot
//
//@Composable
//fun HabitsNavHost(
//    navController: NavHostController,
//    modifier: Modifier = Modifier,
//) {
//    NavHost(
//        navController = navController,
//        // FIX 1: Use the Object Type directly for Type-Safe Navigation
//        startDestination = com.google.android.libraries.mapsplatform.transportation.consumer.model.Route,
//        modifier = modifier,
//    ) {
//        composable<com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Preview> {
//            PreviewScreen(
//                onAnimationFinished = {
//                    navController.navigate(com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Today) {
//                        // FIX 2: Ensure popUpTo matches the class type exactly
//                        popUpTo(com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Preview) { inclusive = true }
//                    }
//                }
//            )
//        }
//
//        composable<com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Today> {
//            TodayScreenRoot(
//                onNavigateToEdit = { habitId ->
//                    navController.navigate(com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.CreateEditHabit(habitId = habitId))
//                },
//                onNavigateToCreate = {
//                    navController.navigate(com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.CreateEditHabit())
//                },
//                onNavigateToStats = {
//                    navController.navigate(com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Statistics)
//                },
//            )
//        }
//
//        composable<com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.Statistics> {
//            StatisticsScreenRoot(
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }
//
//        composable<com.google.android.libraries.mapsplatform.transportation.consumer.model.Route.CreateEditHabit> {
//            CreateEditHabitScreenRoot(
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }
//    }
//}