package io.peng.sparrowdelivery.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.ui.theme.ShadcnTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingFlow(
    userName: String = "",
    onOnboardingComplete: () -> Unit,
    onSkipToLocationSetup: () -> Unit = onOnboardingComplete
) {
    val pages = remember { OnboardingData.getOnboardingPages(userName) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()
    
    ShadcnTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content with horizontal pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    isVisible = pagerState.currentPage == pageIndex
                )
            }
            
            // Bottom section with navigation
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                OnboardingBottomSection(
                    currentPage = pagerState.currentPage,
                    totalPages = pages.size,
                    isLastPage = pagerState.currentPage == pages.size - 1,
                    onNextClick = {
                        scope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                // Go to next page
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                // Complete onboarding
                                onOnboardingComplete()
                            }
                        }
                    },
                    onSkipClick = {
                        onSkipToLocationSetup()
                    }
                )
            }
        }
    }
}
