package io.peng.sparrowdelivery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import io.peng.sparrowdelivery.integration.DeepLinkDestination
import io.peng.sparrowdelivery.integration.DeepLinkHandler
import io.peng.sparrowdelivery.integration.LocationData
import io.peng.sparrowdelivery.navigation.Screen
import io.peng.sparrowdelivery.navigation.SparrowDeliveryNavigation
import io.peng.sparrowdelivery.ui.theme.SparrowDeliveryTheme

class MainActivity : ComponentActivity() {
    private var externalBookingData: Triple<LocationData, LocationData, String?>? = null
    private var callbackScheme: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SparrowDeliveryTheme {
                val navController = rememberNavController()
                val deepLinkProcessed = remember { mutableStateOf(false) }
                
                // Handle deep links on app launch
                LaunchedEffect(Unit) {
                    if (!deepLinkProcessed.value) {
                        handleDeepLink(intent)
                        deepLinkProcessed.value = true
                    }
                }
                
                SparrowDeliveryNavigation(
                    navController = navController,
                    externalBookingData = externalBookingData,
                    onExternalBookingCallback = { success, bookingId, _ ->
                        returnToExternalApp(success, bookingId, callbackScheme)
                    }
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links when app is already running
        setIntent(intent)
        handleDeepLink(intent)
    }
    
    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            Log.d("MainActivity", "Processing deep link: $uri")
            
            when (val destination = DeepLinkHandler.parse(uri)) {
                is DeepLinkDestination.Chat -> {
                    Log.d("MainActivity", "Navigating to chat for booking: ${destination.bookingId}")
                    // Regular navigation for chat
                }
                is DeepLinkDestination.Track -> {
                    Log.d("MainActivity", "Navigating to tracking for booking: ${destination.bookingId}")
                    // Regular navigation for tracking
                }
                is DeepLinkDestination.Booking -> {
                    Log.d("MainActivity", "Navigating to home for booking: ${destination.bookingId}")
                    // Regular navigation for existing booking
                }
                is DeepLinkDestination.BookingRequest -> {
                    Log.d("MainActivity", "Processing external booking request")
                    externalBookingData = Triple(
                        destination.pickup,
                        destination.dropoff,
                        destination.referenceId
                    )
                    callbackScheme = destination.callbackScheme
                }
                is DeepLinkDestination.Unknown -> {
                    Log.w("MainActivity", "Unknown deep link destination: $uri")
                }
            }
        }
    }
    
    fun returnToExternalApp(success: Boolean, bookingId: String?, callbackScheme: String?) {
        callbackScheme?.let { scheme ->
            try {
                val callbackUri = Uri.parse(scheme)
                    .buildUpon()
                    .appendQueryParameter("success", success.toString())
                    .apply {
                        if (bookingId != null) {
                            appendQueryParameter("booking_id", bookingId)
                        }
                    }
                    .build()
                
                val callbackIntent = Intent(Intent.ACTION_VIEW, callbackUri)
                startActivity(callbackIntent)
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to launch callback: ${e.message}")
            }
        }
    }
}
