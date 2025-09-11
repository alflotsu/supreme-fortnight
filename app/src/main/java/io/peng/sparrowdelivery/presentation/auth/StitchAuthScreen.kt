package io.peng.sparrowdelivery.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.peng.sparrowdelivery.data.auth.GoogleSignInLauncher
import io.peng.sparrowdelivery.data.auth.rememberGoogleSignInClient
import io.peng.sparrowdelivery.ui.components.ContextualHapticFeedback
import io.peng.sparrowdelivery.ui.theme.StitchTheme

/**
 * Stitch-styled authentication screen matching the dark theme reference designs
 * Features: Dark background, specific color palette, proper input styling with icons
 */

// HTML Reference Design Colors (registration_1.html - dark)
object StitchAuthColors {
    val background = Color(0xFF000000) // Pure black bg-black
    val inputBackground = Color(0xFF111827) // bg-gray-900
    val inputBorder = Color(0xFF374151) // border-gray-700
    val accent = Color(0xFF00E699) // --primary-color from HTML
    val textPrimary = Color(0xFFFFFFFF) // text-white
    val textSecondary = Color(0xFF6B7280) // text-gray-500 placeholder
    val divider = Color(0xFF374151) // border-gray-700
}

@Composable
fun StitchAuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
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
        // Contextual haptic feedback
        ContextualHapticFeedback(
            isSuccess = uiState.isLoggedIn,
            isError = uiState.errorMessage != null,
            isLoading = uiState.isLoading,
            intensity = 0.8f
        )
        
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StitchAuthColors.background)
        ) {
            // HTML Reference Layout: Centered content with proper spacing
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Velocity",
                    color = StitchAuthColors.textPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Welcome/Sign up Title (HTML: "Welcome back" or "Create account")
                Text(
                    text = if (uiState.isSignUpMode) "Create account" else "Welcome back",
                    color = StitchAuthColors.textPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                // Form Content (matching HTML max-w-sm width constraint)
                Box(modifier = Modifier.widthIn(max = 400.dp)) {
                    if (uiState.isSignUpMode) {
                        StitchSignUpContent(
                            uiState = uiState,
                            onSignUp = viewModel::signUpWithEmail,
                            onGoogleSignUp = googleSignInLauncher,
                            onClearError = viewModel::clearError
                        )
                    } else {
                        StitchSignInContent(
                            uiState = uiState,
                            onSignIn = viewModel::signInWithEmail,
                            onGoogleSignIn = googleSignInLauncher,
                            onClearError = viewModel::clearError
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                // Footer toggle
                StitchAuthFooter(
                    isSignUpMode = uiState.isSignUpMode,
                    onToggleMode = viewModel::toggleSignUpMode,
                    enabled = !uiState.isLoading
                )
            }
        }
    }
}

@Composable
private fun StitchAuthHeader(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = StitchAuthColors.textPrimary
            )
        }
        
        IconButton(onClick = onHelpClick) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = "Help",
                tint = StitchAuthColors.textPrimary
            )
        }
    }
}

@Composable
private fun StitchSignUpContent(
    uiState: AuthUiState,
    onSignUp: (String, String) -> Unit,
    onGoogleSignUp: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
//        Text(
//            text = "Create an account",
//            color = StitchAuthColors.textPrimary,
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
        // Input fields
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StitchAuthInput(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Full name",
                leadingIcon = Icons.Default.Person,
                enabled = !uiState.isLoading
            )
            
            StitchAuthInput(
                value = email,
                onValueChange = { 
                    email = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Email",
                leadingIcon = Icons.Default.Email,
                enabled = !uiState.isLoading
            )
            
            StitchAuthInput(
                value = password,
                onValueChange = { 
                    password = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                enabled = !uiState.isLoading
            )
//
//            StitchAuthInput(
//                value = confirmPassword,
//                onValueChange = {
//                    confirmPassword = it
//                    if (uiState.errorMessage != null) onClearError()
//                },
//                placeholder = "Confirm password",
//                leadingIcon = Icons.Default.Lock,
//                isPassword = true,
//                enabled = !uiState.isLoading
//            )
            
            StitchAuthInput(
                value = phoneNumber,
                onValueChange = { 
                    phoneNumber = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Phone number",
                leadingIcon = Icons.Default.Phone,
                enabled = !uiState.isLoading
            )
        }
        
        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Sign Up Button
        StitchAuthPrimaryButton(
            text = if (uiState.isLoading) "Creating account..." else "Sign up",
            onClick = { onSignUp(email, password) },
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank() && 
                    fullName.isNotBlank(),
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // Divider with "OR"
        StitchAuthDivider()
        
        // Google Sign Up Button
        StitchAuthSecondaryButton(
            text = "Sign up with Google",
            onClick = onGoogleSignUp,
            enabled = !uiState.isLoading,
            icon = Icons.Default.Email // Replace with Google icon in production
        )
    }
}

@Composable
private fun StitchSignInContent(
    uiState: AuthUiState,
    onSignIn: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input fields matching HTML structure
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StitchAuthInput(
                value = email,
                onValueChange = { 
                    email = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Email or phone",
                leadingIcon = null,
                enabled = !uiState.isLoading
            )
            
            StitchAuthInput(
                value = password,
                onValueChange = { 
                    password = it
                    if (uiState.errorMessage != null) onClearError()
                },
                placeholder = "Password",
                leadingIcon = null,
                isPassword = true,
                enabled = !uiState.isLoading
            )
        }
        
        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        // Primary Login Button (HTML style: rounded-full, accent color)
        StitchAuthPrimaryButton(
            text = if (uiState.isLoading) "Signing in..." else "Log in",
            onClick = { onSignIn(email.trim(), password) },
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
        )
        
        // Divider with "Or continue with"
        StitchAuthDivider()
        
        // Social Login Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StitchAuthSocialButton(
                text = "Google",
                onClick = onGoogleSignIn,
                enabled = !uiState.isLoading,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Email
            )
            
//            StitchAuthSocialButton(
//                text = "Facebook",
//                onClick = { /* TODO: Facebook auth */ },
//                enabled = !uiState.isLoading,
//                modifier = Modifier.weight(1f),
//                icon = Icons.Default.Share
//            )
        }
    }
}

@Composable
private fun StitchAuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPassword: Boolean = false
) {
    // HTML style: form-input w-full rounded-xl border-gray-700 bg-gray-900 h-14 px-4
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = StitchAuthColors.textSecondary
            )
        },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StitchAuthColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = StitchAuthColors.textPrimary),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = StitchAuthColors.inputBackground,
            unfocusedContainerColor = StitchAuthColors.inputBackground,
            disabledContainerColor = StitchAuthColors.inputBackground,
            focusedBorderColor = StitchAuthColors.accent,
            unfocusedBorderColor = StitchAuthColors.inputBorder,
            focusedTextColor = StitchAuthColors.textPrimary,
            unfocusedTextColor = StitchAuthColors.textPrimary,
            cursorColor = StitchAuthColors.accent
        )
    )
}

@Composable
private fun StitchAuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // HTML style: rounded-full bg-[var(--primary-color)] py-4 text-lg font-bold text-black
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StitchAuthColors.accent,
            contentColor = Color.Black,
            disabledContainerColor = StitchAuthColors.accent.copy(alpha = 0.5f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(28.dp) // fully rounded
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StitchAuthSecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StitchAuthColors.inputBackground,
            contentColor = StitchAuthColors.textPrimary,
            disabledContainerColor = StitchAuthColors.inputBackground.copy(alpha = 0.5f),
            disabledContentColor = StitchAuthColors.textPrimary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StitchAuthSocialButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StitchAuthColors.inputBackground,
            contentColor = StitchAuthColors.textPrimary,
            disabledContainerColor = StitchAuthColors.inputBackground.copy(alpha = 0.5f),
            disabledContentColor = StitchAuthColors.textPrimary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StitchAuthDivider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = StitchAuthColors.divider
        )
        
        Text(
            text = "Or continue with",
            color = StitchAuthColors.textSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = StitchAuthColors.divider
        )
    }
}

@Composable
private fun StitchAuthFooter(
    isSignUpMode: Boolean,
    onToggleMode: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp, top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
            color = StitchAuthColors.textSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        TextButton(
            onClick = onToggleMode,
            enabled = enabled
        ) {
            Text(
                text = if (isSignUpMode) "Log in" else "Sign up",
                color = StitchAuthColors.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StitchAuthScreenPreview() {
    StitchAuthScreen(
        onAuthSuccess = {}
    )
}
