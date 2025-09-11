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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import io.peng.sparrowdelivery.ui.animations.*
import io.peng.sparrowdelivery.presentation.auth.StitchAuthScreen
import io.peng.sparrowdelivery.presentation.splash.SplashScreen
import io.peng.sparrowdelivery.presentation.onboarding.OnboardingFlow
import io.peng.sparrowdelivery.presentation.onboarding.LocationSetupScreen
import io.peng.sparrowdelivery.presentation.features.home.HomeScreen
import io.peng.sparrowdelivery.presentation.features.home.MoreOptionsScreen
import io.peng.sparrowdelivery.presentation.features.profile.ProfileScreen
import io.peng.sparrowdelivery.presentation.features.profile.OrderDetailScreen
import io.peng.sparrowdelivery.presentation.features.profile.StitchOrderDetailScreen
import io.peng.sparrowdelivery.presentation.features.history.StitchDeliveryHistoryScreen
// Legacy tracking screen removed - using StitchTrackingScreen instead
import io.peng.sparrowdelivery.presentation.features.tracking.StitchTrackingScreen
import io.peng.sparrowdelivery.presentation.features.tracking.SimpleTrackingLookupScreen
import io.peng.sparrowdelivery.presentation.features.chat.ChatScreen
import io.peng.sparrowdelivery.presentation.components.ComponentDemoScreen
import io.peng.sparrowdelivery.presentation.features.external.ExternalBookingScreen
import io.peng.sparrowdelivery.presentation.features.external.ExternalBookingReviewScreen
import io.peng.sparrowdelivery.presentation.features.testing.ApiTestingScreen
import io.peng.sparrowdelivery.data.preferences.UserPreferencesManager
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.presentation.features.profile.ProfileViewModel
import io.peng.sparrowdelivery.integration.LocationData

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object StitchAuth : Screen("stitch_auth")
    object Onboarding : Screen("onboarding")
    object LocationSetup : Screen("location_setup")
    object Home : Screen("home")
    object MoreOptions : Screen("more_options")
    object Profile : Screen("profile")
    object OrderDetail : Screen("order_detail/{orderId}")
    object StitchOrderDetail : Screen("stitch_order_detail/{orderId}")
    object DeliveryHistory : Screen("delivery_history")
    object StitchTracking : Screen("stitch_tracking")
    object TrackingLookup : Screen("tracking_lookup")
    object Chat : Screen("chat")
    object ComponentDemo : Screen("component_demo")
    object ApiTesting : Screen("api_testing")
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
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                )
            )
        }
    ) {
        // Splash Screen (fade animation as entry point)
        composable(
            Screen.Splash.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            }
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
            enterTransition = {
                scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) {
            StitchAuthScreen(
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
                },
                onApiTestingClick = {
                    navController.navigate(Screen.ApiTesting.route)
                }
            )
        }
        
        // Profile Screen with enhanced slide animation
        composable(
            Screen.Profile.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + scaleOut(
                    targetScale = 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
                    )
                )
            }
        ) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        
        
        // Stitch Tracking Screen with shared element style animation
        composable(
            Screen.StitchTracking.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 3 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
                    )
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight / 4 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + scaleOut(
                    targetScale = 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    )
                )
            }
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
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        delayMillis = 50,
                        easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
                    )
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            }
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
        
        // Component Demo Screen with enhanced slide animation
        composable(
            Screen.ComponentDemo.route,
            enterTransition = { slideInFromRightWithScale() },
            exitTransition = { slideOutToLeftWithScale() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            ComponentDemoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // API Testing Screen with slide animation
        composable(
            Screen.ApiTesting.route,
            enterTransition = { slideInFromRightWithScale() },
            exitTransition = { slideOutToLeftWithScale() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            ApiTestingScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Stitch Order Detail Screen with slide animation
        composable(
            "stitch_order_detail/{orderId}",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "unknown"
            StitchOrderDetailScreen(
                orderId = orderId,
                onBackClick = {
                    navController.popBackStack()
                },
                onShareClick = {
                    // TODO: Implement share functionality
                },
                onContactSupport = {
                    // TODO: Navigate to support or external contact
                },
                onSubmitFeedback = { feedback ->
                    // TODO: Submit feedback to backend
                },
                isLightTheme = false // Can be changed to true for light theme testing
            )
        }
        
        // Delivery History Screen with slide animation
        composable(
            Screen.DeliveryHistory.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() }
        ) {
            StitchDeliveryHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderClick = { orderId ->
                    navController.navigate("stitch_order_detail/$orderId")
                },
                isLightTheme = false // Can be changed to true for light theme testing
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
