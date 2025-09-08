package io.peng.sparrowdelivery.data.services

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate
import kotlinx.coroutines.launch

class RoutingTestActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            RoutingTestScreen()
        }
    }
    
    @Composable
    fun RoutingTestScreen() {
        var testResult by remember { mutableStateOf("Ready to test...") }
        var isLoading by remember { mutableStateOf(false) }
        
        val mapboxService = remember { MapboxDirectionsService() }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mapbox Routing Test",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    isLoading = true
                    testResult = "Testing Mapbox API..."
                    
                    lifecycleScope.launch {
                        try {
                            // Test with Accra coordinates
                            val origin = LocationCoordinate(
                                latitude = 5.6037, 
                                longitude = -0.1870
                            ) // Accra Central
                            
                            val destination = LocationCoordinate(
                                latitude = 5.6500, 
                                longitude = -0.1167
                            ) // East Legon area
                            
                            println("Starting Mapbox test...")
                            val routes = mapboxService.getRouteAlternatives(
                                origin = origin,
                                destination = destination,
                                profile = MapboxProfile.DRIVING
                            )
                            
                            testResult = if (routes.isNotEmpty()) {
                                val firstRoute = routes[0]
                                "SUCCESS!\n" +
                                "Routes found: ${routes.size}\n" +
                                "Distance: ${firstRoute.distanceText}\n" +
                                "Duration: ${firstRoute.durationText}\n" +
                                "Polyline points: ${firstRoute.polylinePoints.size}"
                            } else {
                                "FAILED: No routes returned\n" +
                                "Check Logcat for API details"
                            }
                            
                        } catch (e: Exception) {
                            testResult = "ERROR: ${e.message}\n" +
                            "Check Logcat for full details"
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Test Mapbox Routing")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = testResult,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
