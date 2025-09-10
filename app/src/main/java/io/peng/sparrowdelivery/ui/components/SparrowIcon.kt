package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import io.peng.sparrowdelivery.ui.icons.SparrowIconContext
import io.peng.sparrowdelivery.ui.icons.SparrowIconWeight
import io.peng.sparrowdelivery.ui.icons.SparrowIcons
import io.peng.sparrowdelivery.ui.theme.SparrowTheme

/**
 * SparrowIcon - SF Symbols-inspired icon component
 * 
 * Provides consistent sizing, spacing, and visual hierarchy
 * following Apple's SF Symbols design principles adapted for Android
 */
@Composable
fun SparrowIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = SparrowIcons.Size.Medium,
    tint: Color = LocalContentColor.current,
    weight: SparrowIconWeight = SparrowIconWeight.REGULAR,
    context: SparrowIconContext = SparrowIconContext.ACTION
) {
    // Apply SF Symbols-inspired sizing based on context
    val contextualSize = when (context) {
        SparrowIconContext.NAVIGATION -> SparrowIcons.Size.Medium   // 24dp - prominent for navigation
        SparrowIconContext.ACTION -> SparrowIcons.Size.Regular      // 20dp - standard for buttons
        SparrowIconContext.STATUS -> SparrowIcons.Size.Small        // 16dp - subtle for indicators
        SparrowIconContext.CONTENT -> SparrowIcons.Size.Small       // 16dp - inline with text
        SparrowIconContext.DECORATIVE -> size                       // Custom size for decorative use
    }
    
    // Apply weight-based alpha (simulating SF Symbols weight variations)
    val weightAlpha = when (weight) {
        SparrowIconWeight.LIGHT -> 0.6f     // Subtle, secondary
        SparrowIconWeight.REGULAR -> 1.0f   // Default
        SparrowIconWeight.BOLD -> 1.0f      // Full opacity, but could be larger
        SparrowIconWeight.FILL -> 1.0f      // Full opacity for filled states
    }
    
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(contextualSize),
        tint = tint.copy(alpha = tint.alpha * weightAlpha)
    )
}

/**
 * Specialized icon components for common use cases
 * Following SF Symbols patterns for semantic meaning
 */

@Composable
fun NavigationIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = SparrowTheme.colors.foreground
) {
    SparrowIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        context = SparrowIconContext.NAVIGATION,
        weight = SparrowIconWeight.REGULAR
    )
}

@Composable
fun ActionIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    emphasized: Boolean = false
) {
    SparrowIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        context = SparrowIconContext.ACTION,
        weight = if (emphasized) SparrowIconWeight.BOLD else SparrowIconWeight.REGULAR
    )
}

@Composable
fun StatusIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    subtle: Boolean = true
) {
    SparrowIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        context = SparrowIconContext.STATUS,
        weight = if (subtle) SparrowIconWeight.LIGHT else SparrowIconWeight.REGULAR
    )
}

@Composable
fun ContentIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = SparrowTheme.colors.mutedForeground
) {
    SparrowIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        context = SparrowIconContext.CONTENT,
        weight = SparrowIconWeight.LIGHT
    )
}

/**
 * Delivery-specific icon components with semantic meaning
 */

@Composable
fun DeliveryStatusIcon(
    status: DeliveryStatus,
    modifier: Modifier = Modifier,
    size: Dp = SparrowIcons.Size.Regular
) {
    val (icon, tint, description) = when (status) {
        DeliveryStatus.PENDING -> Triple(
            SparrowIcons.Delivery.Pending,
            SparrowTheme.colors.warning,
            "Delivery pending"
        )
        DeliveryStatus.IN_TRANSIT -> Triple(
            SparrowIcons.Delivery.InTransit,
            SparrowTheme.colors.primary,
            "In transit"
        )
        DeliveryStatus.DELIVERED -> Triple(
            SparrowIcons.Delivery.Delivered,
            SparrowTheme.colors.success,
            "Delivered"
        )
        DeliveryStatus.CANCELLED -> Triple(
            SparrowIcons.Delivery.Cancelled,
            SparrowTheme.colors.destructive,
            "Cancelled"
        )
        DeliveryStatus.DELAYED -> Triple(
            SparrowIcons.Delivery.Delayed,
            SparrowTheme.colors.warning,
            "Delayed"
        )
    }
    
    SparrowIcon(
        imageVector = icon,
        contentDescription = description,
        modifier = modifier,
        size = size,
        tint = tint,
        context = SparrowIconContext.STATUS,
        weight = SparrowIconWeight.REGULAR
    )
}

@Composable
fun VehicleIcon(
    vehicleType: VehicleType,
    modifier: Modifier = Modifier,
    size: Dp = SparrowIcons.Size.Medium,
    tint: Color = SparrowTheme.colors.foreground
) {
    val (icon, description) = when (vehicleType) {
        VehicleType.TRUCK -> SparrowIcons.Delivery.Truck to "Truck delivery"
        VehicleType.CAR -> SparrowIcons.Delivery.Car to "Car delivery"  
        VehicleType.MOTORCYCLE -> SparrowIcons.Delivery.Motorcycle to "Motorcycle delivery"
        VehicleType.BICYCLE -> SparrowIcons.Delivery.Bicycle to "Bicycle delivery"
        VehicleType.VAN -> SparrowIcons.Delivery.Van to "Van delivery"
    }
    
    SparrowIcon(
        imageVector = icon,
        contentDescription = description,
        modifier = modifier,
        size = size,
        tint = tint,
        context = SparrowIconContext.DECORATIVE
    )
}

// Enums for semantic icon usage
enum class DeliveryStatus {
    PENDING,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    DELAYED
}

enum class VehicleType {
    TRUCK,
    CAR,
    MOTORCYCLE,
    BICYCLE,
    VAN
}
