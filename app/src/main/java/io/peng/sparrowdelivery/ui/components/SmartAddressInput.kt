@file:OptIn(ExperimentalMaterial3Api::class)

package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.peng.sparrowdelivery.data.services.PlaceDetails
import io.peng.sparrowdelivery.data.services.PlacePrediction
import io.peng.sparrowdelivery.data.services.PlacesAutocompleteService
import io.peng.sparrowdelivery.ui.theme.SparrowBorderRadius
import io.peng.sparrowdelivery.ui.theme.SparrowSpacing
import io.peng.sparrowdelivery.ui.theme.SparrowTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Smart address with cost optimization:
 * 1. Shows recents & favorites first (free)
 * 2. Only calls expensive APIs when needed
 * 3. Prioritizes user's common locations
 */
@OptIn(FlowPreview::class)
@Composable
fun SmartAddressInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onPlaceSelected: ((PlaceDetails) -> Unit)? = null,
    placeholder: String = "Enter address",
    isPickupField: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Mock data - in real app, this would come from a repository/database
    val recentAddresses = remember {
        listOf(
            SavedAddress(
                id = "1",
                name = "Accra Mall",
                address = "Tetteh Quarshie Interchange, Accra",
                type = SavedAddressType.RECENT,
                icon = Icons.Default.ShoppingCart,
                latitude = 5.61,
                longitude = -0.14,
                useCount = 5
            ),
            SavedAddress(
                id = "2", 
                name = "Kotoka Airport",
                address = "Airport City, Accra",
                type = SavedAddressType.RECENT,
                icon = Icons.Default.Flight,
                latitude = 5.60,
                longitude = -0.17,
                useCount = 3
            ),
            SavedAddress(
                id = "3",
                name = "Circle",
                address = "Kwame Nkrumah Circle, Accra",
                type = SavedAddressType.RECENT,
                icon = Icons.Default.LocationOn,
                latitude = 5.57,
                longitude = -0.23,
                useCount = 8
            )
        )
    }
    
    val favoriteAddresses = remember {
        listOf(
            SavedAddress(
                id = "fav1",
                name = "Home",
                address = "East Legon, Accra",
                type = SavedAddressType.FAVORITE,
                icon = Icons.Default.Home,
                latitude = 5.65,
                longitude = -0.15,
                useCount = 20
            ),
            SavedAddress(
                id = "fav2",
                name = "Office",
                address = "Airport Residential Area, Accra",
                type = SavedAddressType.FAVORITE,
                icon = Icons.Default.Business,
                latitude = 5.59,
                longitude = -0.18,
                useCount = 15
            )
        )
    }
    
    // Places service for API calls
    val placesService = remember { PlacesAutocompleteService(context) }
    
    // UI state
    var showDropdown by remember { mutableStateOf(false) }
    var hasSearchedApi by remember { mutableStateOf(false) }
    
    // API predictions
    val apiPredictions by placesService.predictions.collectAsStateWithLifecycle()
    val isLoadingApi by placesService.isLoading.collectAsStateWithLifecycle()
    val error by placesService.error.collectAsStateWithLifecycle()
    
    // Filter local addresses based on current input
    val filteredRecents = remember(value) {
        if (value.isBlank()) recentAddresses
        else recentAddresses.filter { 
            it.name.contains(value, ignoreCase = true) || 
            it.address.contains(value, ignoreCase = true)
        }
    }
    
    val filteredFavorites = remember(value) {
        if (value.isBlank()) favoriteAddresses
        else favoriteAddresses.filter {
            it.name.contains(value, ignoreCase = true) || 
            it.address.contains(value, ignoreCase = true)
        }
    }
    
    // Combined local results (favorites first, then recents by usage)
    val localResults = remember(filteredFavorites, filteredRecents) {
        (filteredFavorites + filteredRecents.sortedByDescending { it.useCount })
            .take(8) // Limit to keep dropdown manageable
    }
    
    // Debounced search for API calls (with proper dependency tracking)
    val searchQuery = remember { MutableStateFlow("") }
    
    LaunchedEffect(searchQuery) {
        searchQuery
            .debounce(500) // Debounce to reduce API calls
            .collect { query ->
                if (query.isNotBlank()) {
                    hasSearchedApi = true
                    placesService.searchPlaces(query)
                } else {
                    placesService.clearPredictions()
                    hasSearchedApi = false
                }
            }
    }
    
    // Smart trigger: call API only when no local results and user is typing
    LaunchedEffect(value, localResults) {
        if (value.isNotBlank()) {
            showDropdown = true
            
            if (localResults.isEmpty()) {
                // Trigger debounced API search
                searchQuery.value = value
            } else {
                // Clear API results when local results are available
                searchQuery.value = ""
            }
        } else {
            showDropdown = false
            searchQuery.value = ""
        }
    }
    
    // Handle saved address selection with immediate dropdown dismissal
    val handleSavedAddressSelection: (SavedAddress) -> Unit = { address ->
        // Immediately hide dropdown to prevent double-click issues
        showDropdown = false
        onValueChange(address.name)
        
        // Convert to PlaceDetails format
        val placeDetails = PlaceDetails(
            placeId = address.id,
            name = address.name,
            address = address.address,
            latitude = address.latitude,
            longitude = address.longitude,
            placeTypes = emptyList(), // Saved addresses don't have Google Place types
            businessStatus = null // Saved addresses don't have business status
        )
        onPlaceSelected?.invoke(placeDetails)
    }
    
    // Handle API place selection with immediate dropdown dismissal
    val handleApiPlaceSelection: (PlacePrediction) -> Unit = { prediction ->
        // Immediately hide dropdown to prevent double-click issues
        showDropdown = false
        onValueChange(prediction.primaryText)
        
        coroutineScope.launch {
            placesService.fetchPlaceDetails(prediction.placeId)?.let { placeDetails ->
                onPlaceSelected?.invoke(placeDetails)
            }
        }
    }
    
    SparrowTheme {
        ExposedDropdownMenuBox(
            expanded = showDropdown && (localResults.isNotEmpty() || apiPredictions.isNotEmpty() || isLoadingApi),
            onExpandedChange = { expanded ->
                // Sync our state with the native dropdown state
                // This ensures proper handling of external dismissal (clicking outside, back button, etc.)
                if (!expanded) {
                    showDropdown = false
                }
            },
            modifier = modifier
        ) {
            SparrowInput(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                    // Update dropdown visibility based on input
                    if (newValue.isBlank()) {
                        showDropdown = false
                    }
                    // showDropdown will be set to true by LaunchedEffect when there are results to show
                },
                placeholder = placeholder,
                trailingIcon = if (showDropdown && (localResults.isNotEmpty() || apiPredictions.isNotEmpty())) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = showDropdown && (localResults.isNotEmpty() || apiPredictions.isNotEmpty() || isLoadingApi),
                onDismissRequest = { showDropdown = false },
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .clip(RoundedCornerShape(SparrowBorderRadius.md))
                    .background(SparrowTheme.colors.background)
                    .padding(vertical = SparrowSpacing.xs)
            ) {
                // Show local results first (cost optimization)
                if (localResults.isNotEmpty()) {
                    // Favorites section
                    if (filteredFavorites.isNotEmpty()) {
                        SmartAddressMenuSection(
                            title = "⭐ Favorites",
                            subtitle = "${filteredFavorites.size} found"
                        )
                        filteredFavorites.forEach { address ->
                            SmartAddressMenuItem(
                                address = address,
                                onClick = { handleSavedAddressSelection(address) }
                            )
                        }
                        if (filteredRecents.isNotEmpty()) {
                            HorizontalDivider(color = SparrowTheme.colors.border)
                        }
                    }
                    
                    // Recent locations section
                    if (filteredRecents.isNotEmpty()) {
                        SmartAddressMenuSection(
                            title = "📍 Recent",
                            subtitle = "${filteredRecents.size} found"
                        )
                        filteredRecents.sortedByDescending { it.useCount }.forEach { address ->
                            SmartAddressMenuItem(
                                address = address,
                                onClick = { handleSavedAddressSelection(address) }
                            )
                        }
                    }
                    
                    // Separator between local and API results
                    if (apiPredictions.isNotEmpty()) {
                        HorizontalDivider(color = SparrowTheme.colors.border)
                    }
                }
                
                // API results (only when no local matches - cost optimization)
                if (localResults.isEmpty() && hasSearchedApi) {
                    if (isLoadingApi) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SparrowSpacing.md),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = SparrowTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(SparrowSpacing.sm))
                            SmallText(
                                text = "Searching Google Places...",
                                color = SparrowTheme.colors.mutedForeground
                            )
                        }
                    } else {
                        if (apiPredictions.isNotEmpty()) {
                            SmartAddressMenuSection(
                                title = "🔍 Search Results",
                                subtitle = "${apiPredictions.size} found from Google Places"
                            )
                            apiPredictions.take(5).forEach { prediction ->
                                ApiPredictionMenuItem(
                                    prediction = prediction,
                                    onClick = { handleApiPlaceSelection(prediction) }
                                )
                            }
                        } else {
                            // Show "searched but no results" state
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SparrowSpacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = SparrowTheme.colors.mutedForeground,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(SparrowSpacing.xs))
                                SmallText(
                                    text = "No places found",
                                    color = SparrowTheme.colors.mutedForeground
                                )
                                SmallText(
                                    text = "Try a different search term",
                                    color = SparrowTheme.colors.mutedForeground
                                )
                            }
                        }
                    }
                }
            }
        }
            
            // Error message below input field (normal document flow)
            error?.let { errorMessage ->
                SmallText(
                    text = errorMessage,
                    color = SparrowTheme.colors.destructive,
                    modifier = Modifier.padding(top = SparrowSpacing.xs)
                )
            }
        }
}

@Composable
private fun SmartAddressMenuSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = SparrowSpacing.md,
                vertical = SparrowSpacing.sm
            )
            .background(
                color = SparrowTheme.colors.muted.copy(alpha = 0.3f),
                shape = RoundedCornerShape(SparrowBorderRadius.sm)
            )
            .padding(SparrowSpacing.sm)
    ) {
        SmallText(
            text = title,
            color = SparrowTheme.colors.primary
        )
        SmallText(
            text = subtitle,
            color = SparrowTheme.colors.mutedForeground
        )
    }
}

@Composable
private fun SmartAddressMenuItem(
    address: SavedAddress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = address.icon,
                    contentDescription = null,
                    tint = when (address.type) {
                        SavedAddressType.FAVORITE -> SparrowTheme.colors.warning
                        SavedAddressType.RECENT -> SparrowTheme.colors.primary
                    },
                    modifier = Modifier.size(20.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Paragraph(
                            text = address.name,
                            color = SparrowTheme.colors.foreground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        // Usage indicator
                        SmallText(
                            text = when (address.type) {
                                SavedAddressType.FAVORITE -> "★"
                                SavedAddressType.RECENT -> "${address.useCount}x"
                            },
                            color = when (address.type) {
                                SavedAddressType.FAVORITE -> SparrowTheme.colors.warning
                                SavedAddressType.RECENT -> SparrowTheme.colors.mutedForeground
                            }
                        )
                    }
                    
                    SmallText(
                        text = address.address,
                        color = SparrowTheme.colors.mutedForeground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ApiPredictionMenuItem(
    prediction: PlacePrediction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = getIconForPlaceType(prediction.icon),
                    contentDescription = null,
                    tint = SparrowTheme.colors.mutedForeground,
                    modifier = Modifier.size(20.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Paragraph(
                        text = prediction.primaryText,
                        color = SparrowTheme.colors.foreground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (prediction.secondaryText.isNotBlank()) {
                        SmallText(
                            text = prediction.secondaryText,
                            color = SparrowTheme.colors.mutedForeground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Distance indicator
                prediction.distanceMeters?.let { distance ->
                    val distanceText = when {
                        distance < 1000 -> "${distance}m"
                        else -> "${"%.1f".format(distance / 1000.0)}km"
                    }
                    SmallText(
                        text = distanceText,
                        color = SparrowTheme.colors.mutedForeground
                    )
                }
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}

// Data classes for saved addresses
data class SavedAddress(
    val id: String,
    val name: String,
    val address: String,
    val type: SavedAddressType,
    val icon: ImageVector,
    val latitude: Double,
    val longitude: Double,
    val useCount: Int = 1,
    val isFavorite: Boolean = type == SavedAddressType.FAVORITE
)

enum class SavedAddressType {
    FAVORITE,
    RECENT
}

// Helper function for place type icons (reuse from existing code)
private fun getIconForPlaceType(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "airport" -> Icons.Default.Flight
        "shopping" -> Icons.Default.ShoppingCart
        "hospital" -> Icons.Default.LocalHospital
        "school" -> Icons.Default.School
        "restaurant" -> Icons.Default.Restaurant
        "gas_station" -> Icons.Default.LocalGasStation
        "bank" -> Icons.Default.AccountBalance
        "hotel" -> Icons.Default.Hotel
        else -> Icons.Default.LocationOn
    }
}
