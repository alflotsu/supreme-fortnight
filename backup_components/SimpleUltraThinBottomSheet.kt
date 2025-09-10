package io.peng.sparrowdelivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.theme.LocalStitchColorScheme
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * Opaque bottom sheet using Stitch design system
 * Clean and modern design with proper contrast
 * Based on the beautiful designs in styles/reference/
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpaqueBottomSheetScaffold(
    state: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    sheetPeekHeight: androidx.compose.ui.unit.Dp = 120.dp,
    sheetContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    BottomSheetScaffold(
        scaffoldState = state,
        sheetPeekHeight = sheetPeekHeight,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        modifier = modifier,
        sheetContent = {
            // Stitch-style opaque bottom sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = sheetPeekHeight)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        // Solid background color
                        stitchColors.surface,
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    // Stitch-style handle indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(stitchColors.outline.copy(alpha = 0.6f))
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Sheet content
                    sheetContent()
                }
            }
        }
    ) { paddingValues ->
        // Map content - no padding so it fills entire screen
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content(paddingValues)
        }
    }
}

/**
 * Translucent card for overlays (finding drivers, etc.) using Stitch design
 */
@Composable
fun TranslucentCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    val cardBackground = backgroundColor ?: stitchColors.overlay
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun OpaqueBottomSheetPreview() {
    StitchTheme {
        val scaffoldState = rememberBottomSheetScaffoldState()
        val stitchColors = LocalStitchColorScheme.current
        
        OpaqueBottomSheetScaffold(
            state = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Reliable delivery at your fingertips",
                        style = MaterialTheme.typography.titleLarge,
                        color = stitchColors.onSurface
                    )
                    Text(
                        text = "Opaque bottom sheet with Stitch design system",
                        style = MaterialTheme.typography.bodyMedium,
                        color = stitchColors.textSecondary
                    )
                    
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = stitchColors.primary,
                            contentColor = stitchColors.onPrimary
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Request Delivery",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Simulated map background with Stitch cream color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(stitchColors.accent.copy(alpha = 0.2f))
                    // Don't apply paddingValues - let map fill screen
            ) {
                // Map content would go here
            }
        }
    }
}
