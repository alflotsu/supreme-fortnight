package io.peng.sparrowdelivery.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.data.auth.GoogleSignInLauncher
import io.peng.sparrowdelivery.data.auth.rememberGoogleSignInClient
import io.peng.sparrowdelivery.ui.components.*
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.EnhancedAnimatedCard
import io.peng.sparrowdelivery.ui.components.AnimatedHeroSection
import io.peng.sparrowdelivery.ui.components.ContextualHapticFeedback

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Google Sign-In setup
    val googleSignInClient = rememberGoogleSignInClient(context)
    val googleSignInLauncher = GoogleSignInLauncher(
        googleSignInClient = googleSignInClient,
        onResult = { result ->
            result.fold(
                onSuccess = { account -> viewModel.handleGoogleSignInSuccess(account) },
                onFailure = { exception -> viewModel.handleGoogleSignInFailure(exception) }
            )
        }
    )
    
    // Navigate to main app when logged in
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onAuthSuccess()
        }
    }
    
    StitchTheme {
        // Add contextual haptic feedback for auth states
        ContextualHapticFeedback(
            isSuccess = uiState.isLoggedIn,
            isError = uiState.errorMessage != null,
            isLoading = uiState.isLoading,
            intensity = 0.8f
        )
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero section with animated background
            AnimatedHeroSection(
                title = "Welcome to Sparrow",
                subtitle = "Fast, reliable delivery at your fingertips",
                modifier = Modifier.fillMaxWidth(),
                backgroundContent = {
                    // Subtle gradient background
                },
                actions = {}
            )
            
            // Animated auth card
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EnhancedAnimatedCard(
                    onClick = { /* No-op for container */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    hapticFeedback = false,
                    elevationAnimation = false,
                    scaleAnimation = false
                ) {
                    AuthContent(
                        uiState = uiState,
                        onSignUp = viewModel::signUpWithEmail,
                        onSignIn = viewModel::signInWithEmail,
                        onGoogleSignIn = googleSignInLauncher,
                        onToggleMode = viewModel::toggleSignUpMode,
                        onClearError = viewModel::clearError
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onSignUp: (String, String) -> Unit,
    onSignIn: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onToggleMode: () -> Unit,
    onClearError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val stitchColors = LocalStitchColorScheme.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShadcnHeading(
                text = "Velourcity",
                level = 1,
                color = stitchColors.primary
            )
            SText(
                text = if (uiState.isSignUpMode) "Create your account" else "Welcome back",
                style = TextStyle.Large,
                color = stitchColors.textSecondary
            )
        }
        
        // Email input
        ShadcnEmailInput(
            value = email,
            onValueChange = { 
                email = it
                if (uiState.errorMessage != null) onClearError()
            },
            label = "Email Address",
            placeholder = "Enter your email",
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )
        
        // Password input
        ShadcnPasswordInput(
            value = password,
            onValueChange = { 
                password = it
                if (uiState.errorMessage != null) onClearError()
            },
            label = "Password",
            placeholder = "Enter your password",
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )
        
        // Error message
        if (uiState.errorMessage != null) {
            InlineAlert(
                message = uiState.errorMessage,
                variant = AlertVariant.Destructive,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Action buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Primary action button
            SparrowTextButton(
                text = if (uiState.isLoading) "Please wait..." else if (uiState.isSignUpMode) "Sign Up" else "Sign In",
                onClick = {
                    if (uiState.isSignUpMode) {
                        onSignUp(email, password)
                    } else {
                        onSignIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Default,
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
            )
            
            // Google Sign In button
            SparrowTextButton(
                text = "Continue with Google",
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Outline,
                enabled = !uiState.isLoading,
                leadingIcon = Icons.Default.Email // Replace with Google icon in production
            )
        }
        
        // Toggle mode
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SText(
                text = if (uiState.isSignUpMode) "Already have an account?" else "Don't have an account?",
                style = TextStyle.Small,
                color = stitchColors.textSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            SparrowTextButton(
                text = if (uiState.isSignUpMode) "Sign In" else "Sign Up",
                onClick = onToggleMode,
                variant = ButtonVariant.Link,
                size = ButtonSize.Small,
                enabled = !uiState.isLoading
            )
        }
    }
}
