package io.peng.sparrowdelivery.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.peng.sparrowdelivery.data.services.*
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(FlowPreview::class)
@Composable
fun AddressAutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    onPlaceSelected: ((PlaceDetails) -> Unit)? = null,
    placeholder: String = "Enter address",
    isPickupField: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Places service instance - ideally this would be provided via DI
    val placesService = remember { PlacesAutocompleteService(context) }
    
    // UI state for autocomplete
    var showDropdown by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    
    // Predictions from the service
    val predictions by placesService.predictions.collectAsStateWithLifecycle()
    val isLoading by placesService.isLoading.collectAsStateWithLifecycle()
    val error by placesService.error.collectAsStateWithLifecycle()
    
    // Debounce search queries
    val searchQuery = remember { MutableStateFlow("") }
    
    LaunchedEffect(searchQuery) {
        searchQuery
            .debounce(300) // Wait 300ms after user stops typing
            .collect { query ->
                if (query.isNotBlank()) {
                    isSearching = true
                    placesService.searchPlaces(query)
                    isSearching = false
                } else {
                    placesService.clearPredictions()
                }
            }
    }
    
    // Update search query when value changes
    LaunchedEffect(value) {
        searchQuery.value = value
        showDropdown = value.isNotBlank()
    }
    
    // Handle place selection
    val handlePlaceSelection: (PlacePrediction) -> Unit = { prediction ->
        // First, update the field with the prediction text
        onValueChange(prediction.primaryText)
        showDropdown = false
        
        // Then fetch detailed place information
        coroutineScope.launch {
            placesService.fetchPlaceDetails(prediction.placeId)?.let { placeDetails ->
                onPlaceSelected?.invoke(placeDetails)
            }
        }
    }
    
    Box(modifier = modifier) {
        Column {
            // Text input field
            ShadcnTextField(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                    showDropdown = newValue.isNotBlank()
                },
                placeholder = placeholder,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Autocomplete dropdown
            if (showDropdown && predictions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .animateContentSize()
                        .zIndex(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 240.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        items(predictions.take(5)) { prediction ->
                            PredictionItem(
                                prediction = prediction,
                                onClick = { handlePlaceSelection(prediction) }
                            )
                        }
                    }
                }
            }
            
            // Error message
            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PredictionItem(
    prediction: PlacePrediction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Place type icon
        Icon(
            imageVector = getIconForPlaceType(prediction.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Address details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prediction.primaryText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (prediction.secondaryText.isNotBlank()) {
                Text(
                    text = prediction.secondaryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        
        // Distance indicator (if available)
        prediction.distanceMeters?.let { distance ->
            val distanceText = when {
                distance < 1000 -> "${distance}m"
                distance < 10000 -> "${"%.1f".format(distance / 1000.0)}km"
                else -> "${(distance / 1000)}km"
            }
            
            Text(
                text = distanceText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}

// Helper function to map place type strings to Material Icons
@Composable
private fun getIconForPlaceType(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "restaurant" -> Icons.Default.ShoppingCart // Using available alternative
        "local_hospital" -> Icons.Default.Build    // Using available alternative
        "account_balance" -> Icons.Default.AccountCircle // Using available alternative
        "local_gas_station" -> Icons.Default.Build // Using available alternative
        "shopping_bag" -> Icons.Default.ShoppingCart
        "school" -> Icons.Default.AccountCircle    // Using available alternative
        "hotel" -> Icons.Default.Home             // Using available alternative
        "directions_bus" -> Icons.Default.AccountCircle // Using available alternative
        "business" -> Icons.Default.AccountCircle // Using available alternative
        "location_on" -> Icons.Default.LocationOn
        else -> Icons.Default.Place
    }
}
