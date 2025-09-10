import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*

@Composable
fun FeedbackScreen(
    onBackClick: () -> Unit,
    onSubmitFeedback: (FeedbackData) -> Unit
) {
    var feedbackType by remember { mutableStateOf(FeedbackType.GENERAL) }
    var rating by remember { mutableStateOf(5) }
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
    SparrowTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SparrowTheme.colors.background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SparrowSpacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.md)
                ) {
                    ShadcnIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = onBackClick,
                        contentDescription = "Back"
                    )
                    ShadcnHeading(
                        text = "Send Feedback",
                        level = 3
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(SparrowSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(SparrowSpacing.lg)
            ) {
                SparrowCard(
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnCardVariant.Default
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Feedback Type Selection
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ShadcnText(
                                text = "Feedback Type",
                                style = ShadcnTextStyle.H4
                            )
                            
                            ShadcnSelect(
                                options = FeedbackType.entries.map { 
                                    ShadcnSelectOption(it, it.displayName, it.description) 
                                },
                                selectedOption = ShadcnSelectOption(feedbackType, feedbackType.displayName, feedbackType.description),
                                onOptionSelected = { onFeedbackTypeChange(it.value) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        HorizontalDivider()
                        
                        // Rating
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ShadcnText(
                                text = "Overall Rating",
                                style = ShadcnTextStyle.H4
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                RatingBar(
                                    rating = rating,
                                    onRatingChange = onRatingChange
                                )
                            }
                            
                            ShadcnText(
                                text = when (rating) {
                                    1 -> "Poor"
                                    2 -> "Fair"
                                    3 -> "Good"
                                    4 -> "Very Good"
                                    5 -> "Excellent"
                                    else -> ""
                                },
                                style = ShadcnTextStyle.P,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        HorizontalDivider()
                        
                        // Feedback Text
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ShadcnText(
                                text = "Your Feedback",
                                style = ShadcnTextStyle.H4
                            )
                            
                            ShadcnTextField(
                                value = feedbackText,
                                onValueChange = onFeedbackTextChange,
                                label = "Describe your experience",
                                placeholder = "Please share your detailed feedback...",
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 6
                            )
                        }
                    }
                }
                
                // Submit Button
                ShadcnButton(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = feedbackText.isNotBlank()
                ) {
                    Text(text = "Submit Feedback")
                }
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..5) {
            ShadcnIconButton(
                icon = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                onClick = { onRatingChange(i) },
                contentDescription = "Rate $i stars"
            )
        }
    }
}

@Composable
private fun FeedbackConfirmationScreen(
    onBackToFeedback: () -> Unit,
    onExit: () -> Unit
) {
    SparrowTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SparrowTheme.colors.background)
                .padding(SparrowSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(SparrowTheme.colors.success.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SparrowTheme.colors.success,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            ShadcnHeading(
                text = "Thank You!",
                level = 2,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            ShadcnText(
                text = "Your feedback has been submitted successfully. We appreciate you taking the time to help us improve our service.",
                style = ShadcnTextStyle.P,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShadcnButton(
                    onClick = onBackToFeedback,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ShadcnButtonVariant.Outline
                ) {
                    Text(text = "Send More Feedback")
                }
                
                ShadcnButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Back to App")
                }
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
