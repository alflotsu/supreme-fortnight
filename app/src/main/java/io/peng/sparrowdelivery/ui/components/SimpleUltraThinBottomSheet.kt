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
import io.peng.sparrowdelivery.ui.theme.ShadcnColors
import io.peng.sparrowdelivery.ui.theme.ShadcnTheme

/**
 * Ultra-thin translucent bottom sheet 
 * Inspired by sheet.txt - will add proper Haze blur later
 * Perfect for overlaying on maps while maintaining visibility
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltraThinBottomSheetScaffold(
    state: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    sheetPeekHeight: androidx.compose.ui.unit.Dp = 120.dp,
    sheetContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = state,
        sheetPeekHeight = sheetPeekHeight,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        modifier = modifier,
        sheetContent = {
            // Ultra-thin frosted glass effect (simplified)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = sheetPeekHeight)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        // Translucent white with high transparency
                        Color.White.copy(alpha = 0.75f),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    // Subtle handle indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Gray.copy(alpha = 0.4f))
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
 * Translucent card for overlays (finding drivers, etc.)
 */
@Composable
fun TranslucentCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.85f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor, RoundedCornerShape(16.dp))
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
private fun UltraThinBottomSheetPreview() {
    ShadcnTheme {
        val scaffoldState = rememberBottomSheetScaffoldState()
        
        UltraThinBottomSheetScaffold(
            state = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Ultra-thin translucent glass",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Map content shows through beautifully",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Find Driver")
                    }
                }
            }
        ) { paddingValues ->
            // Simulated map background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green.copy(alpha = 0.3f))
                    // Don't apply paddingValues - let map fill screen
            ) {
                // Your map would go here
            }
        }
    }
}
