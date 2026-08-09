package com.example.ui.navigation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.DataStoreManager
import com.example.database.AppDatabase
import com.example.domain.repository.DeviceRepository
import com.example.ui.automation.MacroBuilderScreen
import com.example.ui.automation.MacroViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.remote.RemoteViewModel
import com.example.ui.remote.UniversalRemoteScreen
import com.example.ui.settings.DeveloperModeScreen
import com.example.ui.settings.PrivacyScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.setup.AddDeviceWizardScreen
import com.example.ui.setup.SetupViewModel

object Routes {
    const val HOME = "home"
    const val REMOTE = "remote/{deviceId}"
    const val ADD_DEVICE = "add_device"
    const val MACROS = "macros"
    const val SETTINGS = "settings"
    const val DEV_MODE = "dev_mode"
    const val PRIVACY = "privacy"
    const val COMPATIBILITY = "compatibility"

    fun remoteRoute(deviceId: String) = "remote/$deviceId"
}

@Composable
fun AppNavigation(
    context: Context,
    repository: DeviceRepository,
    dataStoreManager: DataStoreManager
) {
    val navController = rememberNavController()
    val settings by dataStoreManager.settingsFlow.collectAsState(initial = com.example.data.AppSettings())

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController, 
            startDestination = Routes.HOME,
            enterTransition = { 
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) 
            },
            exitTransition = { 
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) 
            },
            popEnterTransition = { 
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) 
            },
            popExitTransition = { 
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) 
            }
        ) {
        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository, dataStoreManager, context)
            )
            HomeScreen(
                viewModel = vm,
                onNavigateToAddDevice = { navigateToTab(Routes.ADD_DEVICE) },
                onNavigateToRemote = { deviceId -> navController.navigate(Routes.remoteRoute(deviceId)) },
                onNavigateToSettings = { navigateToTab(Routes.SETTINGS) },
                onNavigateToAutomation = { navigateToTab(Routes.MACROS) }
            )
        }

        composable(
            route = Routes.REMOTE,
            arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            val vm: RemoteViewModel = viewModel(
                factory = RemoteViewModel.Factory(deviceId, repository, dataStoreManager, context)
            )
            UniversalRemoteScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Routes.HOME) },
                onNavigateToAddDevice = { navigateToTab(Routes.ADD_DEVICE) },
                onNavigateToAutomation = { navigateToTab(Routes.MACROS) },
                onNavigateToSettings = { navigateToTab(Routes.SETTINGS) }
            )
        }

        composable(Routes.ADD_DEVICE) {
            val vm: SetupViewModel = viewModel(
                factory = SetupViewModel.Factory(repository, context)
            )
            AddDeviceWizardScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Routes.HOME) },
                onNavigateToRemote = { deviceId -> navController.navigate(Routes.remoteRoute(deviceId)) },
                onNavigateToAutomation = { navigateToTab(Routes.MACROS) },
                onNavigateToSettings = { navigateToTab(Routes.SETTINGS) },
                onSetupComplete = { navigateToTab(Routes.HOME) }
            )
        }

        composable(Routes.MACROS) {
            val vm: MacroViewModel = viewModel(
                factory = MacroViewModel.Factory(repository, context)
            )
            MacroBuilderScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Routes.HOME) },
                onNavigateToRemote = { navigateToTab(Routes.HOME) }, // Placeholder since remote requires ID
                onNavigateToAddDevice = { navigateToTab(Routes.ADD_DEVICE) },
                onNavigateToSettings = { navigateToTab(Routes.SETTINGS) }
            )
        }

        composable(Routes.SETTINGS) {
            val vm: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(dataStoreManager, context)
            )
            SettingsScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navigateToTab(Routes.HOME) },
                onNavigateToRemote = { navigateToTab(Routes.HOME) }, // Placeholder since remote requires ID
                onNavigateToAddDevice = { navigateToTab(Routes.ADD_DEVICE) },
                onNavigateToAutomation = { navigateToTab(Routes.MACROS) },
                onNavigateToDeveloperMode = { navController.navigate(Routes.DEV_MODE) },
                onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) },
                onNavigateToCompatibility = { navController.navigate(Routes.COMPATIBILITY) }
            )
        }

        composable(Routes.DEV_MODE) {
            val vm: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(dataStoreManager, context)
            )
            DeveloperModeScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PRIVACY) {
            PrivacyScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.COMPATIBILITY) {
            com.example.ui.compatibility.CompatibilityScreen(onNavigateBack = { navController.popBackStack() })
        }
    }

    com.example.ui.components.LayoutDebugOverlay(enabled = settings.layoutDebugEnabled)
    }
}
