package io.peng.sparrowdelivery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.peng.sparrowdelivery.ui.animations.*
import io.peng.sparrowdelivery.presentation.auth.AuthScreen
import io.peng.sparrowdelivery.presentation.splash.SplashScreen
import io.peng.sparrowdelivery.presentation.onboarding.OnboardingFlow
import io.peng.sparrowdelivery.presentation.onboarding.LocationSetupScreen
import io.peng.sparrowdelivery.presentation.features.home.HomeScreen
import io.peng.sparrowdelivery.presentation.features.home.MoreOptionsScreen
import io.peng.sparrowdelivery.presentation.features.profile.ProfileScreen
import io.peng.sparrowdelivery.presentation.features.profile.OrderDetailScreen
import io.peng.sparrowdelivery.presentation.features.tracking.TrackingScreen
import io.peng.sparrowdelivery.presentation.features.tracking.StitchTrackingScreen
import io.peng.sparrowdelivery.presentation.features.tracking.SimpleTrackingLookupScreen
import io.peng.sparrowdelivery.presentation.features.chat.ChatScreen
import io.peng.sparrowdelivery.presentation.components.ComponentDemoScreen
import io.peng.sparrowdelivery.presentation.features.external.ExternalBookingScreen
import io.peng.sparrowdelivery.presentation.features.external.ExternalBookingReviewScreen
import io.peng.sparrowdelivery.data.preferences.UserPreferencesManager
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.presentation.features.profile.ProfileViewModel
import io.peng.sparrowdelivery.integration.LocationData

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Onboarding : Screen("onboarding")
    object LocationSetup : Screen("location_setup")
    object Home : Screen("home")
    object MoreOptions : Screen("more_options")
    object Profile : Screen("profile")
    object OrderDetail : Screen("order_detail/{orderId}")
    object Tracking : Screen("tracking")
    object StitchTracking : Screen("stitch_tracking")
    object TrackingLookup : Screen("tracking_lookup")
    object Chat : Screen("chat")
    object ComponentDemo : Screen("component_demo")
    object ExternalBookingReview : Screen("external_booking_review")
    object ExternalBooking : Screen("external_booking")
}

@Composable
fun SparrowDeliveryNavigation(
    navController: NavHostController,
    externalBookingData: Triple<LocationData, LocationData, String?>? = null,
    onExternalBookingCallback: ((Boolean, String?, String?) -> Unit)? = null
) {
    val context = LocalContext.current
    val preferencesManager = remember { UserPreferencesManager(context) }
    val onboardingPrefs by preferencesManager.userOnboardingPrefs.collectAsState(
        initial = io.peng.sparrowdelivery.presentation.onboarding.UserOnboardingPrefs()
    )
    val userName by preferencesManager.userName.collectAsState(initial = "")
    
    // Determine starting destination based on external booking or onboarding status
    val startDestination = when {
        externalBookingData != null -> Screen.ExternalBookingReview.route
        !onboardingPrefs.hasCompletedOnboarding -> Screen.Splash.route
        else -> Screen.Home.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInFromRight() },
        exitTransition = { slideOutToLeft() },
        popEnterTransition = { slideInFromLeft() },
        popExitTransition = { slideOutToRight() }
    ) {
        // Splash Screen (no animation, as it's the entry point)
        composable(
            Screen.Splash.route,
            enterTransition = { fadeInTransition() },
            exitTransition = { fadeOutTransition() }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Authentication Screen with scale animation
        composable(
            Screen.Auth.route,
            enterTransition = { scaleInTransition() },
            exitTransition = { slideOutToLeft() }
        ) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding Flow with slide animation
        composable(
            Screen.Onboarding.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) {
            OnboardingFlow(
                userName = userName.takeIf { it.isNotBlank() } ?: "there",
                onOnboardingComplete = {
                    navController.navigate(Screen.LocationSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onSkipToLocationSetup = {
                    navController.navigate(Screen.LocationSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Location Setup Screen with slide animation
        composable(
            Screen.LocationSetup.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) {
            LocationSetupScreen(
                onLocationSet = { address, label ->
                    // Save preferences and navigate to home
                    kotlinx.coroutines.runBlocking {
                        preferencesManager.saveLocationPreferences(address, label)
                        preferencesManager.completeOnboarding()
                    }
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationSetup.route) { inclusive = true }
                    }
                },
                onSkip = {
                    // Complete onboarding without location
                    kotlinx.coroutines.runBlocking {
                        preferencesManager.completeOnboarding()
                    }
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationSetup.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen with fade animation (main destination)
        composable(
            Screen.Home.route,
            enterTransition = { fadeInTransition() },
            exitTransition = { fadeOutTransition() },
            popEnterTransition = { fadeInTransition() }
        ) {
            HomeScreen(
                onMoreOptionsClick = {
                    navController.navigate(Screen.MoreOptions.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToTracking = {
                    navController.navigate(Screen.StitchTracking.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                },
                onNavigateToComponentDemo = {
                    navController.navigate(Screen.ComponentDemo.route)
                }
            )
        }
        
        // More Options Screen with slide animation
        composable(
            Screen.MoreOptions.route,
            enterTransition = { slideInFromBottom() },
            exitTransition = { slideOutToBottom() }
        ) {
            MoreOptionsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile Screen with slide animation
        composable(
            Screen.Profile.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() }
        ) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Tracking Screen with scale animation
        composable(
            Screen.Tracking.route,
            enterTransition = { scaleInTransition() },
            exitTransition = { scaleOutTransition() }
        ) {
            TrackingScreen(
                driverInfo = io.peng.sparrowdelivery.presentation.features.home.DriverInfo(
                    name = "Kwame Asante",
                    rating = 4.8f,
                    vehicleType = "Toyota Camry",
                    plateNumber = "GR-4587-20",
                    phone = "+233-24-123-4567",
                    totalPrice = 25.50,
                    estimatedArrival = "5-8 mins"
                ),
                pickupLocation = "Accra Mall, Tetteh Quarshie",
                dropoffLocation = "University of Ghana, Legon",
                onBackClick = {
                    navController.popBackStack()
                },
                onMessageClick = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }
        
        // Stitch Tracking Screen with beautiful scale animation
        composable(
            Screen.StitchTracking.route,
            enterTransition = { scaleInTransition() },
            exitTransition = { scaleOutTransition() }
        ) {
            StitchTrackingScreen(
                driverInfo = io.peng.sparrowdelivery.presentation.features.home.DriverInfo(
                    name = "Kwame Asante",
                    rating = 4.8f,
                    vehicleType = "Toyota Camry",
                    plateNumber = "GR-4587-20",
                    phone = "+233-24-123-4567",
                    totalPrice = 25.50,
                    estimatedArrival = "5-8 mins"
                ),
                pickupLocation = "Accra Mall, Tetteh Quarshie",
                dropoffLocation = "University of Ghana, Legon",
                onBackClick = {
                    navController.popBackStack()
                },
                onCallDriverClick = {
                    // TODO: Implement calling functionality
                },
                onMessageDriverClick = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }
        
        // Tracking Lookup Screen with slide animation
        composable(
            Screen.TrackingLookup.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() }
        ) {
            SimpleTrackingLookupScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onChatClick = { delivery ->
                    // Navigate to chat with delivery-specific context
                    navController.navigate(Screen.Chat.route)
                },
                onCallDriverClick = { phoneNumber ->
                    // TODO: Implement actual phone calling functionality
                    // This would typically use an Intent to open the phone dialer
                }
            )
        }
        
        // Chat Screen with slide up animation
        composable(
            Screen.Chat.route,
            enterTransition = { slideInFromBottom() },
            exitTransition = { slideOutToBottom() }
        ) {
            ChatScreen(
                driverName = "Kwame Asante",
                driverPhone = "+233-24-123-4567",
                driverVehicle = "Toyota Camry",
                driverPlateNumber = "GR-4587-20",
                onBackClick = {
                    navController.popBackStack()
                },
                onCallClick = {
                    // TODO: Implement calling functionality
                }
            )
        }
        
        // Component Demo Screen with scale animation
        composable(
            Screen.ComponentDemo.route,
            enterTransition = { scaleInTransition() },
            exitTransition = { scaleOutTransition() }
        ) {
            ComponentDemoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // External Booking Review Screen (Step 1: Review and confirm)
        composable(
            Screen.ExternalBookingReview.route,
            enterTransition = { scaleInTransition() },
            exitTransition = { scaleOutTransition() }
        ) {
            externalBookingData?.let { (pickup, dropoff, referenceId) ->
                ExternalBookingReviewScreen(
                    pickup = pickup,
                    dropoff = dropoff,
                    referenceId = referenceId,
                    onConfirmBooking = {
                        // Navigate to the actual booking screen
                        navController.navigate(Screen.ExternalBooking.route)
                    },
                    onCancel = {
                        // Handle callback to external app
                        onExternalBookingCallback?.invoke(false, null, null)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.ExternalBookingReview.route) { inclusive = true }
                        }
                    }
                )
            } ?: run {
                // If no external booking data, redirect to home
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ExternalBookingReview.route) { inclusive = true }
                    }
                }
            }
        }
        
        // External Booking Screen (Step 2: Find drivers and complete)
        composable(
            Screen.ExternalBooking.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() }
        ) {
            externalBookingData?.let { (pickup, dropoff, referenceId) ->
                ExternalBookingScreen(
                    pickup = pickup,
                    dropoff = dropoff,
                    referenceId = referenceId,
                    onCompleted = { success, bookingId ->
                        // Handle callback to external app
                        onExternalBookingCallback?.invoke(success, bookingId, null)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.ExternalBooking.route) { inclusive = true }
                        }
                    },
                    onCancel = {
                        // Go back to review screen (allow user to make changes)
                        navController.popBackStack(Screen.ExternalBookingReview.route, false)
                    }
                )
            } ?: run {
                // If no external booking data, redirect to home
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ExternalBooking.route) { inclusive = true }
                    }
                }
            }
        }
        
        // Note: OrderDetail navigation is handled via callback for now
        // In a real app, you would use a proper navigation pattern with shared ViewModels
    }
}
