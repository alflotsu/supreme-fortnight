package io.peng.sparrowdelivery.presentation.features.testing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import io.peng.sparrowdelivery.domain.entities.RouteProvider
import io.peng.sparrowdelivery.domain.entities.RouteRequest
import io.peng.sparrowdelivery.domain.entities.TransportMode
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * API Testing Screen for testing different routing providers
 * Perfect for testing your polyline implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestingScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    var selectedProvider by remember { mutableStateOf<RouteProvider?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<String?>(null) }
    
    // Ghana test coordinates (KNUST campus area)
    val knustCampus = LatLng(6.6745, -1.5716)
    val studentHostel = LatLng(6.6833, -1.5647)
    
    StitchTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        StitchText(
                            text = "API Testing",
                            style = StitchTextStyle.H4,
                            color = stitchColors.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = stitchColors.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = stitchColors.surface
                    )
                )
            },
            containerColor = stitchColors.background
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Provider Selection Card
                StitchCard(
                    variant = StitchCardVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StitchHeading(
                            text = "Select Routing API Provider",
                            level = 3
                        )
                        
                        StitchText(
                            text = "Choose which API to test for route calculation between KNUST campus locations",
                            style = StitchTextStyle.P,
                            color = stitchColors.textSecondary
                        )
                        
                        ApiProviderDropdown(
                            selectedProvider = selectedProvider,
                            onProviderSelected = { provider ->
                                selectedProvider = provider
                                lastResult = null // Clear previous results
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Test Route Card
                StitchCard(
                    variant = StitchCardVariant.Outlined,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StitchHeading(
                            text = "Test Route Calculation",
                            level = 3
                        )
                        
                        // Route details
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RoutePointCard(
                                title = "From: KNUST Campus",
                                subtitle = "Latitude: ${knustCampus.latitude}, Longitude: ${knustCampus.longitude}",
                                isOrigin = true
                            )
                            
                            RoutePointCard(
                                title = "To: Student Hostel",
                                subtitle = "Latitude: ${studentHostel.latitude}, Longitude: ${studentHostel.longitude}",
                                isOrigin = false
                            )
                        }
                        
                        // Test Button
                        StitchPrimaryButton(
                            onClick = {
                                selectedProvider?.let { provider ->
                                    isLoading = true
                                    // Simulate API call - replace with actual repository call
                                    testApiCall(provider) { result ->
                                        lastResult = result
                                        isLoading = false
                                    }
                                }
                            },
                            text = if (isLoading) "Testing..." else "Test Route Calculation",
                            enabled = selectedProvider != null && !isLoading,
                            loading = isLoading,
                            icon = if (!isLoading) Icons.Default.PlayArrow else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Results Card
                lastResult?.let { result ->
                    StitchCard(
                        variant = StitchCardVariant.Filled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StitchHeading(
                                text = "Test Results",
                                level = 4
                            )
                            
                            StitchText(
                                text = result,
                                style = StitchTextStyle.P,
                                color = stitchColors.onSurface
                            )
                        }
                    }
                }
                
                // API Information Card
                selectedProvider?.let { provider ->
                    ApiInformationCard(provider = provider)
                }
            }
        }
    }
}

@Composable
private fun RoutePointCard(
    title: String,
    subtitle: String,
    isOrigin: Boolean,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Point indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    if (isOrigin) stitchColors.success else stitchColors.error,
                    androidx.compose.foundation.shape.CircleShape
                )
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            StitchText(
                text = title,
                style = StitchTextStyle.P,
                color = stitchColors.onSurface
            )
            StitchText(
                text = subtitle,
                style = StitchTextStyle.Small,
                color = stitchColors.textMuted
            )
        }
    }
}

@Composable
private fun ApiInformationCard(
    provider: RouteProvider,
    modifier: Modifier = Modifier
) {
    val stitchColors = LocalStitchColorScheme.current
    
    val (title, description, pros, cons) = when (provider) {
        RouteProvider.GOOGLE_MAPS -> ApiInfo(
            title = "Google Maps Directions API",
            description = "Premium routing with comprehensive coverage",
            pros = listOf(
                "Best coverage worldwide",
                "Native polyline format",
                "Real-time traffic data",
                "Highly accurate"
            ),
            cons = listOf(
                "More expensive",
                "Lower free tier",
                "Requires credit card",
                "Usage-based pricing"
            )
        )
    }
    
    StitchCard(
        variant = StitchCardVariant.Outlined,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchHeading(
                text = title,
                level = 4
            )
            
            StitchText(
                text = description,
                style = StitchTextStyle.P,
                color = stitchColors.textSecondary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pros
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StitchText(
                        text = "✅ Advantages",
                        style = StitchTextStyle.Small,
                        color = stitchColors.success
                    )
                    pros.forEach { pro ->
                        StitchText(
                            text = "• $pro",
                            style = StitchTextStyle.Small,
                            color = stitchColors.textMuted
                        )
                    }
                }
                
                // Cons
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StitchText(
                        text = "⚠️ Considerations",
                        style = StitchTextStyle.Small,
                        color = stitchColors.error
                    )
                    cons.forEach { con ->
                        StitchText(
                            text = "• $con",
                            style = StitchTextStyle.Small,
                            color = stitchColors.textMuted
                        )
                    }
                }
            }
        }
    }
}

private data class ApiInfo(
    val title: String,
    val description: String,
    val pros: List<String>,
    val cons: List<String>
)

/**
 * Simulate API call for testing
 * Replace this with actual RouteRepository call when ready
 */
private fun testApiCall(
    provider: RouteProvider,
    onResult: (String) -> Unit
) {
    // Simulate API delay
    CoroutineScope(Dispatchers.Main).launch {
        delay(2000)
        
        val mockResult = when (provider) {
            RouteProvider.GOOGLE_MAPS -> """
                ✅ Google Maps API Test Success!
                
                Route Distance: 1.18 km
                Duration: 3 minutes  
                Encoded Polyline: "u{~vFvyys@fS..."
                Format: Native Google format
                
                Status: Direct display ready
            """.trimIndent()
        }
        
        onResult(mockResult)
    }
}

@Preview(showBackground = true)
@Composable
private fun ApiTestingScreenPreview() {
    ApiTestingScreen()
}
