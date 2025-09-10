package io.peng.sparrowdelivery.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.AnimatedHeroSection

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isVisible: Boolean = true
) {
    // Use the AnimatedHeroSection for enhanced presentation
    AnimatedHeroSection(
        title = page.title,
        subtitle = page.description,
        modifier = Modifier.fillMaxSize(),
        backgroundContent = {
            // Background icon with subtle animation
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.icon,
                    fontSize = 200.sp,
                    color = SparrowTheme.colors.primary.copy(alpha = 0.05f),
                    textAlign = TextAlign.Center
                )
            }
        },
        actions = {
            // Optional: Add action content here if needed
        }
    )
}

@Composable
fun OnboardingProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SparrowSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            val animatedWidth by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "progress_indicator_width"
            )
            
            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(8.dp)
                    .background(
                        color = if (isActive) 
                            SparrowTheme.colors.primary 
                        else 
                            SparrowTheme.colors.muted,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun OnboardingBottomSection(
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    isLastPage: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SparrowSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressIndicator(
            currentPage = currentPage,
            totalPages = totalPages
        )
        
        Spacer(modifier = Modifier.height(SparrowSpacing.xl))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skip button
            if (!isLastPage) {
                SparrowTextButton(
                    text = "Skip",
                    onClick = onSkipClick,
                    variant = ShadcnButtonVariant.Ghost,
                    size = ShadcnButtonSize.Large
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            // Next/Get Started button
            SparrowTextButton(
                text = if (isLastPage) "Get Started" else "Next",
                onClick = onNextClick,
                variant = ShadcnButtonVariant.Default,
                size = ShadcnButtonSize.Large,
                modifier = Modifier.widthIn(min = 120.dp)
            )
        }
    }
}
