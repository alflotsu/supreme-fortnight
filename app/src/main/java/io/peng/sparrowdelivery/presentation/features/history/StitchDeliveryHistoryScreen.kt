package io.peng.sparrowdelivery.presentation.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.ui.theme.StitchTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Delivery history screen matching HTML reference designs delivery_history_1 and delivery_history_2
 * Features dark theme list layout with status indicators and navigation
 */

// Stitch Design Colors for History Screen (matching HTML)
object StitchHistoryColors {
    val background = Color(0xFF000000) // bg-black
    val surface = Color(0xFF18181B) // bg-zinc-900 for cards
    val surfaceLight = Color(0xFFFFFFFF) // bg-white for light theme
    val cardBackground = Color(0xFF18181B) // bg-zinc-900
    val cardBackgroundLight = Color(0xFFFFFFFF) // bg-white
    val cardBorder = Color(0xFF27272A) // border-zinc-800
    val cardBorderLight = Color(0xFFE4E4E7) // border-zinc-200
    val textPrimary = Color(0xFFF4F4F5) // text-zinc-100
    val textPrimaryLight = Color(0xFF18181B) // text-zinc-800
    val textSecondary = Color(0xFF9CA3AF) // text-zinc-400
    val textSecondaryLight = Color(0xFF71717A) // text-zinc-500
    val accent = Color(0xFF00E699) // --primary-color
    val accentRed = Color(0xFFEA2A33) // --primary-color red variant
    
    // Status colors
    val statusDelivered = Color(0xFF22C55E) // bg-green-500
    val statusDeliveredText = Color(0xFF4ADE80) // text-green-400
    val statusDeliveredTextLight = Color(0xFF16A34A) // text-green-600
    val statusInTransit = Color(0xFFF97316) // bg-orange-500
    val statusInTransitText = Color(0xFFFB923C) // text-orange-400
    val statusInTransitTextLight = Color(0xFFEA580C) // text-orange-600
    val statusCancelled = Color(0xFFEF4444) // bg-red-500
    val statusCancelledText = Color(0xFFF87171) // text-red-400
    val statusCancelledTextLight = Color(0xFFDC2626) // text-red-600
    val chevronColor = Color(0xFF71717A) // text-zinc-500
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StitchDeliveryHistoryScreen(
    onBackClick: () -> Unit,
    onOrderClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = false // Toggle between dark/light theme variants
) {
    // Sample delivery history data
    val deliveries = remember { createSampleDeliveries() }
    
    StitchTheme {
        val backgroundColor = if (isLightTheme) StitchHistoryColors.surfaceLight else StitchHistoryColors.background
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Header with backdrop blur (HTML: header class="sticky top-0 z-10 bg-black/80 backdrop-blur-sm")
            StitchHistoryHeader(
                title = "History",
                onBackClick = onBackClick,
                isLightTheme = isLightTheme
            )
            
            // Main content (HTML: main class="flex-1 px-4 pt-4")
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(deliveries) { delivery ->
                    StitchDeliveryHistoryItem(
                        delivery = delivery,
                        onClick = { onOrderClick(delivery.id) },
                        isLightTheme = isLightTheme
                    )
                }
            }
            
            // Bottom Navigation (HTML: nav class="sticky bottom-0 bg-black/80 backdrop-blur-sm")
            StitchHistoryBottomNavigation(isLightTheme = isLightTheme)
        }
    }
}

@Composable
private fun StitchHistoryHeader(
    title: String,
    onBackClick: () -> Unit,
    isLightTheme: Boolean
) {
    val backgroundColor = if (isLightTheme) {
        StitchHistoryColors.surfaceLight.copy(alpha = 0.8f)
    } else {
        StitchHistoryColors.background.copy(alpha = 0.8f)
    }
    val textColor = if (isLightTheme) StitchHistoryColors.textPrimaryLight else StitchHistoryColors.textPrimary
    val buttonColor = if (isLightTheme) Color(0xFFF4F4F5) else Color(0xFF27272A) // bg-zinc-100 / bg-zinc-800
    val iconColor = if (isLightTheme) Color(0xFF27272A) else Color(0xFF9CA3AF) // text-zinc-800 / text-zinc-300
    
    // HTML: header class="sticky top-0 z-10 bg-black/80 backdrop-blur-sm"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button (HTML: button class="flex size-10 shrink-0 items-center justify-center rounded-full bg-zinc-800 text-zinc-300")
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(buttonColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Title (HTML: h1 class="flex-1 text-center text-xl font-bold text-zinc-100")
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            // Spacer to center title (HTML: div class="w-10")
            Spacer(Modifier.width(40.dp))
        }
    }
}

@Composable
private fun StitchDeliveryHistoryItem(
    delivery: DeliveryHistoryItem,
    onClick: () -> Unit,
    isLightTheme: Boolean
) {
    val cardBackground = if (isLightTheme) StitchHistoryColors.cardBackgroundLight else StitchHistoryColors.cardBackground
    val cardBorder = if (isLightTheme) StitchHistoryColors.cardBorderLight else StitchHistoryColors.cardBorder
    val textPrimary = if (isLightTheme) StitchHistoryColors.textPrimaryLight else StitchHistoryColors.textPrimary
    val textSecondary = if (isLightTheme) StitchHistoryColors.textSecondaryLight else StitchHistoryColors.textSecondary
    
    // Status colors based on theme
    val (statusDotColor, statusTextColor) = when (delivery.status) {
        DeliveryStatus.DELIVERED -> {
            StitchHistoryColors.statusDelivered to 
            if (isLightTheme) StitchHistoryColors.statusDeliveredTextLight else StitchHistoryColors.statusDeliveredText
        }
        DeliveryStatus.IN_TRANSIT -> {
            StitchHistoryColors.statusInTransit to 
            if (isLightTheme) StitchHistoryColors.statusInTransitTextLight else StitchHistoryColors.statusInTransitText
        }
        DeliveryStatus.CANCELLED -> {
            StitchHistoryColors.statusCancelled to 
            if (isLightTheme) StitchHistoryColors.statusCancelledTextLight else StitchHistoryColors.statusCancelledText
        }
    }
    
    // HTML: div class="flex items-center justify-between rounded-xl border border-zinc-800 bg-zinc-900 p-4"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main content (HTML: div class="flex-1")
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // From address (HTML: p class="text-base font-semibold text-zinc-100")
                Text(
                    text = "From: ${delivery.fromAddress}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                
                // To address (HTML: p class="text-sm text-zinc-400")
                Text(
                    text = "To: ${delivery.toAddress}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
                
                // Status row (HTML: div class="mt-2 flex items-center gap-2")
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status dot (HTML: span class="inline-block h-2 w-2 rounded-full bg-green-500")
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusDotColor, CircleShape)
                    )
                    
                    // Status text (HTML: p class="text-sm font-medium text-green-400")
                    Text(
                        text = delivery.status.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = statusTextColor
                    )
                }
            }
            
            // Price and date (HTML: div class="text-right")
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Price (HTML: p class="text-sm font-medium text-zinc-100")
                Text(
                    text = formatCurrency(delivery.price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                )
                
                // Date (HTML: p class="text-xs text-zinc-400")
                Text(
                    text = delivery.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }
            
            // Chevron button (HTML: button class="ml-4 text-zinc-500")
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = StitchHistoryColors.chevronColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StitchHistoryBottomNavigation(isLightTheme: Boolean) {
    val backgroundColor = if (isLightTheme) {
        StitchHistoryColors.surfaceLight.copy(alpha = 0.8f)
    } else {
        StitchHistoryColors.background.copy(alpha = 0.8f)
    }
    val borderColor = if (isLightTheme) StitchHistoryColors.cardBorderLight else StitchHistoryColors.cardBorder
    val inactiveColor = if (isLightTheme) Color(0xFF71717A) else Color(0xFF71717A) // text-zinc-500
    val activeColor = if (isLightTheme) StitchHistoryColors.accentRed else StitchHistoryColors.accent
    val activeBgColor = if (isLightTheme) Color(0xFFFEE2E2) else Color(0xFF14532D) // bg-red-100 / bg-green-900/50
    
    Column {
        // HTML: nav class="sticky bottom-0 bg-black/80 backdrop-blur-sm"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            // Tab bar (HTML: div class="flex justify-around border-t border-zinc-800 px-2 py-2")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    NavTabItem("Home", Icons.Default.Home, false),
                    NavTabItem("Track", Icons.Outlined.LocalShipping, false),
                    NavTabItem("History", Icons.Default.History, true), // Active
                    NavTabItem("Profile", Icons.Default.Person, false)
                ).forEach { item ->
                    StitchNavTab(
                        item = item,
                        inactiveColor = inactiveColor,
                        activeColor = activeColor,
                        activeBgColor = activeBgColor
                    )
                }
            }
        }
        
        // Bottom spacer (HTML: div class="h-3 bg-black")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(backgroundColor)
        )
    }
}

@Composable
private fun StitchNavTab(
    item: NavTabItem,
    inactiveColor: Color,
    activeColor: Color,
    activeBgColor: Color
) {
    val backgroundColor = if (item.isActive) activeBgColor else Color.Transparent
    val textColor = if (item.isActive) activeColor else inactiveColor
    
    // HTML: a class="flex flex-1 flex-col items-center justify-center gap-1 rounded-full p-2"
    Column(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontSize = 12.sp
        )
    }
}

// Data Classes
data class DeliveryHistoryItem(
    val id: String,
    val fromAddress: String,
    val toAddress: String,
    val status: DeliveryStatus,
    val price: Double,
    val date: Date
) {
    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
}

enum class DeliveryStatus(val displayName: String) {
    DELIVERED("Delivered"),
    IN_TRANSIT("In Transit"),
    CANCELLED("Cancelled")
}

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val isActive: Boolean
)

// Helper Functions
private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

private fun createSampleDeliveries(): List<DeliveryHistoryItem> {
    return listOf(
        DeliveryHistoryItem(
            id = "1",
            fromAddress = "123 Main St",
            toAddress = "789 Oak Ave",
            status = DeliveryStatus.DELIVERED,
            price = 12.50,
            date = Calendar.getInstance().apply { 
                set(2023, Calendar.DECEMBER, 12) 
            }.time
        ),
        DeliveryHistoryItem(
            id = "2",
            fromAddress = "456 Oak Ave",
            toAddress = "101 Pine Ln",
            status = DeliveryStatus.DELIVERED,
            price = 8.75,
            date = Calendar.getInstance().apply { 
                set(2023, Calendar.NOVEMBER, 28) 
            }.time
        ),
        DeliveryHistoryItem(
            id = "3",
            fromAddress = "789 Pine Ln",
            toAddress = "222 Maple St",
            status = DeliveryStatus.IN_TRANSIT,
            price = 15.00,
            date = Calendar.getInstance().apply { 
                set(2023, Calendar.NOVEMBER, 15) 
            }.time
        ),
        DeliveryHistoryItem(
            id = "4",
            fromAddress = "101 Elm St",
            toAddress = "333 Birch Rd",
            status = DeliveryStatus.CANCELLED,
            price = 9.20,
            date = Calendar.getInstance().apply { 
                set(2023, Calendar.OCTOBER, 30) 
            }.time
        ),
        DeliveryHistoryItem(
            id = "5",
            fromAddress = "222 Maple Ave",
            toAddress = "444 Cedar Ct",
            status = DeliveryStatus.DELIVERED,
            price = 21.30,
            date = Calendar.getInstance().apply { 
                set(2023, Calendar.OCTOBER, 15) 
            }.time
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchDeliveryHistoryScreenPreview() {
    StitchDeliveryHistoryScreen(
        onBackClick = {},
        onOrderClick = {},
        isLightTheme = false
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchDeliveryHistoryScreenLightPreview() {
    StitchDeliveryHistoryScreen(
        onBackClick = {},
        onOrderClick = {},
        isLightTheme = true
    )
}
