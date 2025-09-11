package io.peng.sparrowdelivery.presentation.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.R
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.StitchTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Order details screen matching HTML reference designs order_details_1 through order_details_5
 * Features dark theme, timeline, cost breakdown, driver info, and feedback options
 *  with proper light theme support
 */

// Stitch Design Colors matching HTML with proper light/dark theme support
object StitchOrderColors {
    // Dark mode colors (order_details_1.html)
    val backgroundDark = Color(0xFF121212) // bg-[#121212]
    val surfaceDark = Color(0xFF181818) // bg-[#181818] for footer
    val cardBackgroundDark = Color(0xFF1F1F1F) // Slightly lighter for cards
    val dividerDark = Color(0xFF3F3F46) // border-zinc-800
    val textPrimaryDark = Color(0xFFFFFFFF) // text-white
    val textSecondaryDark = Color(0xFF9CA3AF) // text-zinc-400
    val iconBackgroundDark = Color(0xFF27272A) // bg-zinc-800
    
    // Light mode colors (following delivery_history_2.html light theme pattern)
    val backgroundLight = Color(0xFFFFFFFF) // bg-white
    val surfaceLight = Color(0xFFF8F9FA) // bg-gray-50 for footer
    val cardBackgroundLight = Color(0xFFFFFFFF) // bg-white
    val dividerLight = Color(0xFFE5E7EB) // border-gray-200
    val textPrimaryLight = Color(0xFF111827) // text-gray-900
    val textSecondaryLight = Color(0xFF6B7280) // text-gray-500
    val iconBackgroundLight = Color(0xFFF3F4F6) // bg-gray-100
    
    // Accent colors (same for both themes)
    val accent = Color(0xFF22C55E) // text-green-400, bg-green-500
    val accentDark = Color(0xFF16A34A) // hover:bg-green-600
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StitchOrderDetailScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onSubmitFeedback: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = false
) {
    // Sample order data - in real app this would come from ViewModel/Repository
    val order = remember { createSampleOrder(orderId) }
    
    StitchTheme {
        val backgroundColor = if (isLightTheme) StitchOrderColors.backgroundLight else StitchOrderColors.backgroundDark
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Header (HTML: header class="flex items-center p-4")
            StitchOrderHeader(
                title = "Order Details",
                onBackClick = onBackClick,
                onShareClick = if (order.canShare) onShareClick else null,
                isLightTheme = isLightTheme
            )
            
            // Main Content (HTML: main class="space-y-6 px-4 pb-6")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                
                // Pickup/Dropoff Section
                StitchLocationSection(
                    pickup = order.pickupLocation,
                    dropoff = order.dropoffLocation,
                    isLightTheme = isLightTheme
                )
                
                StitchDivider(isLightTheme = isLightTheme)
                
                // Details Section (Driver + Package)
                StitchDetailsSection(
                    driver = order.driver,
                    packageInfo = order.packageInfo,
                    isLightTheme = isLightTheme
                )
                
                StitchDivider(isLightTheme = isLightTheme)
                
                // Cost Breakdown
                StitchCostBreakdownSection(
                    costBreakdown = order.costBreakdown,
                    isLightTheme = isLightTheme
                )
                
                StitchDivider(isLightTheme = isLightTheme)
                
                // Timeline
                StitchTimelineSection(
                    timeline = order.timeline,
                    isLightTheme = isLightTheme
                )
                
                // Conditional Sections based on order state
                when (order.status) {
                    OrderDetailStatus.DELIVERED -> {
                        StitchDivider(isLightTheme = isLightTheme)
                        StitchFeedbackSection(
                            onSubmitFeedback = onSubmitFeedback,
                            isLightTheme = isLightTheme
                        )
                    }
                    OrderDetailStatus.CANCELLED, OrderDetailStatus.ISSUE -> {
                        StitchDivider(isLightTheme = isLightTheme)
                        StitchContactSupportSection(
                            onContactSupport = onContactSupport,
                            isLightTheme = isLightTheme
                        )
                    }
                    else -> {
                        // No additional section for in-progress orders
                    }
                }
                
                Spacer(Modifier.height(24.dp))
            }
            
            // Bottom Navigation (HTML: footer class="sticky bottom-0")
            StitchBottomNavigation(isLightTheme = isLightTheme)
        }
    }
}

@Composable
private fun StitchOrderHeader(
    title: String,
    onBackClick: () -> Unit,
    onShareClick: (() -> Unit)? = null,
    isLightTheme: Boolean = false
) {
    val textColor = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    
    // HTML: header class="flex items-center p-4"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = "Back",
                tint = textColor
            )
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
        
        if (onShareClick != null) {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = textColor
                )
            }
        } else {
            // Placeholder to maintain center alignment
            Spacer(Modifier.width(48.dp))
        }
    }
}

@Composable
private fun StitchLocationSection(
    pickup: LocationInfo,
    dropoff: LocationInfo,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val iconBackground = if (isLightTheme) StitchOrderColors.iconBackgroundLight else StitchOrderColors.iconBackgroundDark
    val dividerColor = if (isLightTheme) StitchOrderColors.dividerLight else StitchOrderColors.dividerDark
    
    Column {
        // Pickup (HTML: div class="flex items-start gap-4")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Pickup",
                    tint = StitchOrderColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = "Pickup",
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = pickup.address,
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Dashed line connector (HTML: div class="ml-5 h-6 w-px border-l border-dashed border-zinc-700")
        Box(
            modifier = Modifier
                .padding(start = 20.dp)
                .width(1.dp)
                .height(24.dp)
                .background(dividerColor)
        )
        
        // Dropoff
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Dropoff",
                    tint = StitchOrderColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = "Drop-off",
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dropoff.address,
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun StitchDetailsSection(
    driver: DriverInfo,
    packageInfo: PackageInfo,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val iconBackground = if (isLightTheme) StitchOrderColors.iconBackgroundLight else StitchOrderColors.iconBackgroundDark
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StitchHeading(
            text = "Details",
            level = 2,
            color = textPrimary
        )
        
        // Driver Info (HTML: div class="flex items-center gap-4")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver photo (HTML: img class="h-12 w-12 rounded-full object-cover")
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Driver",
                    tint = StitchOrderColors.accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = driver.name,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "License Plate: ${driver.licensePlate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
            }
        }
        
        // Package Info (HTML: div class="flex items-center gap-4")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = "Package",
                    tint = StitchOrderColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = packageInfo.type,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Weight: ${packageInfo.weight}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
            }
        }
    }
}

@Composable
private fun StitchCostBreakdownSection(
    costBreakdown: CostBreakdown,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val dividerColor = if (isLightTheme) StitchOrderColors.dividerLight else StitchOrderColors.dividerDark
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StitchHeading(
            text = "Cost Breakdown",
            level = 2,
            color = textPrimary
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Individual cost items (HTML: div class="flex justify-between")
            costBreakdown.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.label,
                        color = textSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatCurrency(item.amount),
                        color = textSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Total (HTML: div class="flex justify-between border-t border-zinc-800 pt-2 font-bold text-white")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatCurrency(costBreakdown.total),
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun StitchTimelineSection(
    timeline: List<TimelineItem>,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StitchHeading(
            text = "Timeline",
            level = 2,
            color = textPrimary
        )
        
        Column {
            timeline.forEachIndexed { index, item ->
                StitchTimelineItem(
                    item = item,
                    isLast = index == timeline.lastIndex,
                    isLightTheme = isLightTheme
                )
                if (index < timeline.lastIndex) {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StitchTimelineItem(
    item: TimelineItem,
    isLast: Boolean,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val iconBackground = if (isLightTheme) StitchOrderColors.iconBackgroundLight else StitchOrderColors.iconBackgroundDark
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timeline marker with connector line
        Box {
            // Marker circle (HTML: div class="relative mt-1 flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-green-500")
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (item.completed) StitchOrderColors.accent else iconBackground,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (item.completed) Color.Black else textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Connector line (CSS: .timeline-item::before)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .offset(x = 15.dp, y = 32.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(Color(0xFF366348)) // CSS background-color: #366348
                )
            }
        }
        
        // Timeline content
        Column {
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary
            )
        }
    }
}

@Composable
private fun StitchFeedbackSection(
    onSubmitFeedback: (String) -> Unit,
    isLightTheme: Boolean = false
) {
    var feedbackText by remember { mutableStateOf("") }
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val inputBackground = if (isLightTheme) StitchOrderColors.iconBackgroundLight else StitchOrderColors.iconBackgroundDark
    val dividerColor = if (isLightTheme) StitchOrderColors.dividerLight else StitchOrderColors.dividerDark
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StitchHeading(
            text = "Feedback",
            level = 2,
            color = textPrimary
        )
        
        // Feedback textarea (HTML: textarea class="w-full rounded-lg border-zinc-700 bg-zinc-800 p-3 text-white placeholder-zinc-500 focus:border-green-500 focus:ring-green-500")
        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "Share your experience...",
                    color = textSecondary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedBorderColor = StitchOrderColors.accent,
                unfocusedBorderColor = dividerColor,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                cursorColor = StitchOrderColors.accent
            ),
            shape = RoundedCornerShape(8.dp)
        )
        
        // Submit button (HTML: button class="w-full rounded-lg bg-green-500 py-3 font-bold text-black transition-colors hover:bg-green-600")
        Button(
            onClick = { onSubmitFeedback(feedbackText) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StitchOrderColors.accent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Submit Feedback",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StitchContactSupportSection(
    onContactSupport: () -> Unit,
    isLightTheme: Boolean = false
) {
    val textPrimary = if (isLightTheme) StitchOrderColors.textPrimaryLight else StitchOrderColors.textPrimaryDark
    val buttonBackground = if (isLightTheme) StitchOrderColors.iconBackgroundLight else StitchOrderColors.iconBackgroundDark
    
    // HTML: button class="flex w-full items-center justify-center gap-2 rounded-lg bg-zinc-800 py-3 text-sm font-semibold text-white transition-colors hover:bg-zinc-700"
    Button(
        onClick = onContactSupport,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonBackground,
            contentColor = textPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = StitchOrderColors.accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Contact Support",
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StitchBottomNavigation(isLightTheme: Boolean = false) {
    val backgroundColor = if (isLightTheme) StitchOrderColors.surfaceLight else StitchOrderColors.surfaceDark
    val textSecondary = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    
    // HTML: footer class="sticky bottom-0"
    Column {
        // Tab bar (HTML: div class="flex justify-around border-t border-zinc-800 bg-[#181818] px-2 py-2")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                NavItem("Home", Icons.Default.Home, false),
                NavItem("Track", Icons.Outlined.LocalShipping, false),
                NavItem("History", Icons.Default.History, true), // Active for order details
                NavItem("Profile", Icons.Default.Person, false)
            ).forEach { item ->
                StitchNavItem(item, isLightTheme)
            }
        }
        
        // Bottom spacer (HTML: div class="h-4 bg-[#181818]")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(backgroundColor)
        )
    }
}

@Composable
private fun StitchNavItem(
    item: NavItem,
    isLightTheme: Boolean = false
) {
    val inactiveColor = if (isLightTheme) StitchOrderColors.textSecondaryLight else StitchOrderColors.textSecondaryDark
    val activeColor = StitchOrderColors.accent
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (item.isActive) activeColor else inactiveColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = item.label,
            fontSize = 12.sp,
            color = if (item.isActive) activeColor else inactiveColor
        )
    }
}

@Composable
private fun StitchDivider(isLightTheme: Boolean = false) {
    // HTML: div class="h-px w-full bg-zinc-800"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isLightTheme) StitchOrderColors.dividerLight else StitchOrderColors.dividerDark)
    )
}

//  Data Classes
data class LocationInfo(
    val address: String
)

data class DriverInfo(
    val name: String,
    val licensePlate: String,
    val photoUrl: String? = null
)

data class PackageInfo(
    val type: String,
    val weight: String
)

data class CostItem(
    val label: String,
    val amount: Double
)

data class CostBreakdown(
    val items: List<CostItem>,
    val total: Double
)

data class TimelineItem(
    val title: String,
    val time: String,
    val icon: ImageVector,
    val completed: Boolean
)

data class OrderDetail(
    val id: String,
    val pickupLocation: LocationInfo,
    val dropoffLocation: LocationInfo,
    val driver: DriverInfo,
    val packageInfo: PackageInfo,
    val costBreakdown: CostBreakdown,
    val timeline: List<TimelineItem>,
    val status: OrderDetailStatus,
    val canShare: Boolean = false
)

enum class OrderDetailStatus {
    PENDING,
    IN_PROGRESS,
    DELIVERED,
    CANCELLED,
    ISSUE
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val isActive: Boolean
)

// Helper Functions
private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

private fun createSampleOrder(orderId: String): OrderDetail {
    return OrderDetail(
        id = orderId,
        pickupLocation = LocationInfo("123 Elm Street, Anytown, USA"),
        dropoffLocation = LocationInfo("456 Oak Avenue, Anytown, USA"),
        driver = DriverInfo("Ethan Carter", "XYZ 123"),
        packageInfo = PackageInfo("Small Package", "2 lbs"),
        costBreakdown = CostBreakdown(
            items = listOf(
                CostItem("Base Fare", 5.00),
                CostItem("Distance Fee", 2.50)
            ),
            total = 7.50
        ),
        timeline = listOf(
            TimelineItem("Package Picked Up", "10:00 AM", Icons.Default.LocalShipping, true),
            TimelineItem("Package In Transit", "10:15 AM", Icons.Default.LocalShipping, true),
            TimelineItem("Package Delivered", "10:30 AM", Icons.Default.Check, true)
        ),
        status = OrderDetailStatus.DELIVERED,
        canShare = true
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchOrderDetailScreenDarkPreview() {
    StitchOrderDetailScreen(
        orderId = "12345",
        onBackClick = {},
        onShareClick = {},
        onContactSupport = {},
        onSubmitFeedback = {},
        isLightTheme = false
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchOrderDetailScreenLightPreview() {
    StitchOrderDetailScreen(
        orderId = "12345",
        onBackClick = {},
        onShareClick = {},
        onContactSupport = {},
        onSubmitFeedback = {},
        isLightTheme = true
    )
}
