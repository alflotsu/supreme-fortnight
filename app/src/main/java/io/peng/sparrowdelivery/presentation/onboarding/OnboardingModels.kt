package io.peng.sparrowdelivery.presentation.onboarding

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: String, // Using emoji for now, can be replaced with actual icons/illustrations
    val backgroundColor: String? = null
)

data class OnboardingState(
    val currentPage: Int = 0,
    val isCompleted: Boolean = false,
    val userName: String = ""
)

object OnboardingData {
    fun getOnboardingPages(userName: String = "there"): List<OnboardingPage> {
        return listOf(
            OnboardingPage(
                title = "Welcome to SparrowDelivery, $userName! 👋",
                description = "Lightning-fast delivery service that brings everything you need right to your door. Let's get started!",
                icon = "🎉"
            ),
            OnboardingPage(
                title = "How It Works",
                description = "📍 Set your pickup location\n📦 Choose what to deliver\n🚗 Track your delivery in real-time",
                icon = "🚀"
            ),
            OnboardingPage(
                title = "Amazing Features",
                description = "💬 Chat with your driver\n📱 Real-time GPS tracking\n⭐ Rate your delivery experience\n🔔 Instant notifications",
                icon = "✨"
            ),
            OnboardingPage(
                title = "Ready to Get Started!",
                description = "Let's set up your delivery preferences so we can provide you with the best possible service.",
                icon = "🎯"
            )
        )
    }
}

// User preferences for onboarding completion and setup
data class UserOnboardingPrefs(
    val hasCompletedOnboarding: Boolean = false,
    val hasSetupLocation: Boolean = false,
    val preferredAddress: String? = null,
    val preferredAddressLabel: String? = null, // "Home", "Work", etc.
    val onboardingCompletedAt: Long? = null
)
