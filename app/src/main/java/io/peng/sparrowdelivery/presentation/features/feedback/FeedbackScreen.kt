package io.peng.sparrowdelivery.presentation.features.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.stitch.*
import io.peng.sparrowdelivery.ui.theme.*

/**
 * Modern FeedbackScreen using pure Stitch Design System
 * Matches the HTML reference designs with dark/light mode support
 */
@Composable
fun FeedbackScreen(
    onBackClick: () -> Unit,
    onSubmitFeedback: (FeedbackData) -> Unit
) {
    var feedbackType by remember { mutableStateOf(FeedbackType.GENERAL) }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    
    if (isSubmitted) {
        FeedbackConfirmationScreen(
            onBackToFeedback = { isSubmitted = false },
            onExit = onBackClick
        )
    } else {
        FeedbackSubmissionScreen(
            feedbackType = feedbackType,
            rating = rating,
            feedbackText = feedbackText,
            onFeedbackTypeChange = { feedbackType = it },
            onRatingChange = { rating = it },
            onFeedbackTextChange = { feedbackText = it },
            onSubmit = {
                onSubmitFeedback(FeedbackData(feedbackType, rating, feedbackText))
                isSubmitted = true
            },
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun FeedbackSubmissionScreen(
    feedbackType: FeedbackType,
    rating: Int,
    feedbackText: String,
    onFeedbackTypeChange: (FeedbackType) -> Unit,
    onRatingChange: (Int) -> Unit,
    onFeedbackTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit
) {
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(stitchColors.background)
        ) {
            // Header
            FeedbackHeader(onBackClick = onBackClick)
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Feedback Type Selection
                FeedbackTypeSection(
                    selectedType = feedbackType,
                    onTypeChange = onFeedbackTypeChange
                )
                
                // Rating Section
                RatingSection(
                    rating = rating,
                    onRatingChange = onRatingChange
                )
                
                // Feedback Text Section
                FeedbackTextSection(
                    feedbackText = feedbackText,
                    onTextChange = onFeedbackTextChange
                )
                
                // Submit Button
                StitchPrimaryButton(
                    text = "Submit Feedback",
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = feedbackText.isNotBlank(),
                    icon = Icons.Default.Send
                )
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeedbackHeader(
    onBackClick: () -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = stitchColors.background.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StitchIconButton(
                icon = Icons.Default.ArrowBack,
                onClick = onBackClick,
                variant = StitchIconButtonVariant.Secondary
            )
            
            StitchHeading(
                text = "Send Feedback",
                level = 2,
                textAlign = TextAlign.Center
            )
            
            // Balance the layout
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun FeedbackTypeSection(
    selectedType: FeedbackType,
    onTypeChange: (FeedbackType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StitchHeading(
            text = "Feedback Type",
            level = 4,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        StitchCard {
            Column {
                FeedbackType.entries.forEachIndexed { index, type ->
                    FeedbackTypeItem(
                        type = type,
                        isSelected = type == selectedType,
                        onClick = { onTypeChange(type) }
                    )
                    
                    if (index < FeedbackType.entries.size - 1) {
                        val stitchColors = LocalStitchColorScheme.current
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            color = stitchColors.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackTypeItem(
    type: FeedbackType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selection indicator
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = stitchColors.primary
            )
        )
        
        // Type info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StitchText(
                text = type.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            StitchText(
                text = type.description,
                style = MaterialTheme.typography.bodyMedium,
                color = stitchColors.textSecondary
            )
        }
    }
}

@Composable
private fun RatingSection(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StitchHeading(
            text = "Overall Rating",
            level = 4,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        StitchCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Star rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { onRatingChange(i) }
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Rate $i stars",
                                tint = if (i <= rating) stitchColors.accent else stitchColors.textMuted,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                // Rating label
                StitchText(
                    text = when (rating) {
                        1 -> "Poor"
                        2 -> "Fair"
                        3 -> "Good"
                        4 -> "Very Good"
                        5 -> "Excellent"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = stitchColors.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FeedbackTextSection(
    feedbackText: String,
    onTextChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StitchHeading(
            text = "Your Feedback",
            level = 4,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        StitchCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StitchTextField(
                    value = feedbackText,
                    onValueChange = onTextChange,
                    label = "Describe your experience",
                    placeholder = "Please share your detailed feedback...",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6,
                    singleLine = false
                )
                
                val stitchColors = LocalStitchColorScheme.current
                StitchText(
                    text = "${feedbackText.length}/500 characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = stitchColors.textMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun FeedbackConfirmationScreen(
    onBackToFeedback: () -> Unit,
    onExit: () -> Unit
) {
    StitchTheme {
        val stitchColors = LocalStitchColorScheme.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(stitchColors.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(stitchColors.accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = stitchColors.accent,
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            StitchHeading(
                text = "Thank You!",
                level = 1,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            StitchText(
                text = "Your feedback has been submitted successfully. We appreciate you taking the time to help us improve our service.",
                style = MaterialTheme.typography.bodyLarge,
                color = stitchColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StitchPrimaryButton(
                    text = "Back to App",
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Home
                )
                
                StitchOutlineButton(
                    text = "Send More Feedback",
                    onClick = onBackToFeedback,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Feedback
                )
            }
        }
    }
}

// Data classes
data class FeedbackData(
    val type: FeedbackType,
    val rating: Int,
    val text: String
)

enum class FeedbackType(val displayName: String, val description: String) {
    GENERAL("General Feedback", "Share your overall experience with our service"),
    DELIVERY("Delivery Experience", "Feedback about your delivery experience"),
    APP("App Experience", "Comments about our mobile app"),
    CUSTOMER_SERVICE("Customer Service", "Feedback about our customer support"),
    FEATURE_REQUEST("Feature Request", "Suggest new features you'd like to see")
}
