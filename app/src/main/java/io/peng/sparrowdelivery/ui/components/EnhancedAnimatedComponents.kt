package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.*
import kotlinx.coroutines.delay

// Enhanced animated card with press effects and hover states
@Composable
fun EnhancedAnimatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedback: Boolean = true,
    elevationAnimation: Boolean = true,
    scaleAnimation: Boolean = true,
    variant: ShadcnCardVariant = ShadcnCardVariant.Default,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (scaleAnimation && isPressed && enabled) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (elevationAnimation && isPressed && enabled) 2.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_elevation"
    )
    
    val colors = SparrowTheme.colors
    val cardColors = when (variant) {
        ShadcnCardVariant.Default -> colors.card
        ShadcnCardVariant.Elevated -> colors.card
        ShadcnCardVariant.Outlined -> colors.card
        ShadcnCardVariant.Ghost -> colors.card
    }
    
    Card(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(SparrowBorderRadius.lg),
                ambientColor = colors.foreground.copy(alpha = 0.1f),
                spotColor = colors.foreground.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                if (hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = cardColors,
            contentColor = colors.cardForeground
        ),
        shape = RoundedCornerShape(SparrowBorderRadius.lg)
    ) {
        Column(
            modifier = Modifier.padding(SparrowSpacing.lg),
            content = content
        )
    }
}

// Animated list item with staggered entry animations
@Composable
fun AnimatedListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    animationDelay: Long = 0L
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    ) {
        EnhancedAnimatedCard(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading icon with scale animation
                leadingIcon?.let { icon ->
                    val iconScale by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "icon_scale"
                    )
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SparrowTheme.colors.primary,
                        modifier = Modifier
                            .scale(iconScale)
                            .size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(SparrowSpacing.md))
                }
                
                // Text content with slide animation
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    val textSlideOffset by animateIntAsState(
                        targetValue = if (visible) 0 else 50,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "text_slide"
                    )
                    
                    Box(
                        modifier = Modifier
                    ) {
                        ShadcnText(
                            text = title,
                            style = ShadcnTextStyle.Large,
                            color = SparrowTheme.colors.foreground
                        )
                    }
                    
                    subtitle?.let { sub ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                        ) {
                            ShadcnText(
                                text = sub,
                                style = ShadcnTextStyle.Small,
                                color = SparrowTheme.colors.mutedForeground
                            )
                        }
                    }
                }
                
                // Trailing content with fade animation
                trailingContent?.let { content ->
                    val trailingAlpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 600,
                            easing = FastOutSlowInEasing
                        ),
                        label = "trailing_alpha"
                    )
                    
                    Box(
                        modifier = Modifier.alpha(trailingAlpha)
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

// Animated list with staggered animations
@Composable
fun <T> StaggeredAnimatedList(
    items: List<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = remember { LazyListState() },
    staggerDelayMs: Long = 100L,
    contentPadding: PaddingValues = PaddingValues(SparrowSpacing.md),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(SparrowSpacing.sm),
    itemContent: @Composable (item: T, index: Int, animationDelay: Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        itemsIndexed(items) { index, item ->
            val animationDelay = index * staggerDelayMs
            itemContent(item, index, animationDelay)
        }
    }
}

// Expandable animated card
@Composable
fun ExpandableAnimatedCard(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ArrowForward,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "icon_rotation"
    )
    
    EnhancedAnimatedCard(
        onClick = onToggle,
        modifier = modifier,
        elevationAnimation = false
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShadcnText(
                text = title,
                style = ShadcnTextStyle.Large,
                color = SparrowTheme.colors.foreground
            )
            
            Icon(
                imageVector = icon,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = SparrowTheme.colors.primary,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotationAngle)
            )
        }
        
        // Expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = SparrowSpacing.md)
            ) {
                expandedContent()
            }
        }
    }
}

// Floating action button with bounce animation
@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = SparrowTheme.colors.primary,
    contentColor: androidx.compose.ui.graphics.Color = SparrowTheme.colors.primaryForeground,
    hapticFeedback: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "fab_scale"
    )
    
    FloatingActionButton(
        onClick = {
            if (hapticFeedback) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier.scale(scale),
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Hero section with parallax-like animation
@Composable
fun AnimatedHeroSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    backgroundContent: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background content
        backgroundContent()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SparrowSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title animation
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                ShadcnHeading(
                    text = title,
                    level = 1,
                    color = SparrowTheme.colors.foreground,
                    modifier = Modifier.padding(bottom = SparrowSpacing.md)
                )
            }
            
            // Subtitle animation
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
            ) {
                ShadcnText(
                    text = subtitle,
                    style = ShadcnTextStyle.Large,
                    color = SparrowTheme.colors.mutedForeground,
                    modifier = Modifier.padding(bottom = SparrowSpacing.xl)
                )
            }
            
            // Actions animation
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.md),
                    content = actions
                )
            }
        }
    }
}
