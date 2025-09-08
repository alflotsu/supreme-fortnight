package io.peng.sparrowdelivery.data.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val context: Context? = null
) {
    private val auth = SupabaseClient.client.auth
    private var googleSignInHelper: GoogleSignInHelper? = null
    
    init {
        context?.let {
            googleSignInHelper = GoogleSignInHelper(it).apply { initialize() }
        }
    }

    // Get current user
    val currentUser: UserInfo?
        get() = auth.currentUserOrNull()

    // Check if user is logged in
    val isLoggedIn: Boolean
        get() = auth.currentUserOrNull() != null

    // Sign up with email and password
    suspend fun signUpWithEmail(email: String, password: String): Result<UserInfo> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            // Get user after signup
            val user = auth.currentUserOrNull()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up completed but user not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign in with email and password
    suspend fun signInWithEmail(email: String, password: String): Result<UserInfo> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            // Get user after signin
            val user = auth.currentUserOrNull()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in completed but user not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign in with Google (OAuth redirect URL)
    suspend fun signInWithGoogleOAuth(): Result<String> {
        return try {
            auth.signInWith(Google)
            Result.success("Google sign in initiated")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sign in with Google ID Token (native Android)
    suspend fun signInWithGoogleIdToken(account: GoogleSignInAccount): Result<UserInfo> {
        return try {
            account.idToken?.let { idToken ->
                auth.signInWith(IDToken) {
                    this.idToken = idToken
                    provider = Google
                }
                val user = auth.currentUserOrNull()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Google sign in completed but user not found"))
                }
            } ?: Result.failure(Exception("Google ID token not available"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper function to get Google Sign-In helper
    fun getGoogleSignInHelper() = googleSignInHelper

    // Sign out
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get user session flow
    fun getUserSession(): Flow<UserInfo?> = flow {
        emit(currentUser)
        // You can add session state listener here if needed
    }
}
