package io.peng.sparrowdelivery.core.error

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLException

/**
 * Comprehensive Error Handling System
 * 
 * Provides user-friendly error messages, retry logic, and network status checking.
 * Designed for production use with proper timeout handling and exponential backoff.
 */

// Sealed class for different types of errors
sealed class AppError(
    val userMessage: String,
    val technicalMessage: String,
    val isRetryable: Boolean = false,
    val suggestedAction: String? = null
) {
    // Network-related errors
    object NoInternet : AppError(
        userMessage = "No internet connection",
        technicalMessage = "Device is not connected to the internet",
        isRetryable = true,
        suggestedAction = "Check your internet connection and try again"
    )
    
    object SlowConnection : AppError(
        userMessage = "Connection is slow",
        technicalMessage = "Request timed out due to slow network",
        isRetryable = true,
        suggestedAction = "Check your internet speed and try again"
    )
    
    object ServerTimeout : AppError(
        userMessage = "Server is taking too long to respond",
        technicalMessage = "Server response timeout",
        isRetryable = true,
        suggestedAction = "Please try again in a moment"
    )
    
    // API-specific errors
    object ApiKeyInvalid : AppError(
        userMessage = "Service temporarily unavailable",
        technicalMessage = "Invalid API key or quota exceeded",
        isRetryable = false,
        suggestedAction = "Please contact support if this persists"
    )
    
    object RateLimitExceeded : AppError(
        userMessage = "Too many requests, please wait",
        technicalMessage = "API rate limit exceeded",
        isRetryable = true,
        suggestedAction = "Wait a moment before trying again"
    )
    
    object LocationNotFound : AppError(
        userMessage = "Location not found",
        technicalMessage = "Unable to find route for given coordinates",
        isRetryable = false,
        suggestedAction = "Please check your pickup and drop-off locations"
    )
    
    // Generic errors
    data class ServerError(val code: Int) : AppError(
        userMessage = "Service temporarily unavailable",
        technicalMessage = "Server error: HTTP $code",
        isRetryable = code in 500..599,
        suggestedAction = "Please try again in a few minutes"
    )
    
    data class Unknown(val exception: Throwable) : AppError(
        userMessage = "Something went wrong",
        technicalMessage = "Unexpected error: ${exception.message}",
        isRetryable = true,
        suggestedAction = "Please try again"
    )
}

// Result wrapper for API calls
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: AppError) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

// Retry configuration
data class RetryConfig(
    val maxAttempts: Int = 3,
    val initialDelayMs: Long = 1000L,
    val maxDelayMs: Long = 10000L,
    val backoffMultiplier: Double = 2.0
)

/**
 * Main error handler class
 */
class ErrorHandler(private val context: Context) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * Check if device has internet connection
     */
    fun hasInternetConnection(): Boolean {
        return try {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get network connection quality
     */
    fun getConnectionQuality(): ConnectionQuality {
        if (!hasInternetConnection()) {
            return ConnectionQuality.NONE
        }
        
        return try {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            
            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> ConnectionQuality.WIFI
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                    // For simplicity, assume cellular is good. In production, you might check signal strength
                    ConnectionQuality.CELLULAR_GOOD
                }
                else -> ConnectionQuality.POOR
            }
        } catch (e: Exception) {
            ConnectionQuality.POOR
        }
    }
    
    /**
     * Convert exceptions to user-friendly AppError
     */
    fun handleException(exception: Throwable): AppError {
        return when (exception) {
            is UnknownHostException -> {
                if (hasInternetConnection()) {
                    AppError.ServerError(0) // DNS resolution failed
                } else {
                    AppError.NoInternet
                }
            }
            
            is ConnectException -> AppError.NoInternet
            
            is SocketTimeoutException, is TimeoutException -> {
                when (getConnectionQuality()) {
                    ConnectionQuality.NONE -> AppError.NoInternet
                    ConnectionQuality.POOR -> AppError.SlowConnection
                    else -> AppError.ServerTimeout
                }
            }
            
            is SSLException -> AppError.ServerError(0)
            
            is HttpException -> {
                when (exception.code()) {
                    400 -> AppError.LocationNotFound
                    401, 403 -> AppError.ApiKeyInvalid
                    429 -> AppError.RateLimitExceeded
                    in 500..599 -> AppError.ServerError(exception.code())
                    else -> AppError.ServerError(exception.code())
                }
            }
            
            is IOException -> {
                if (hasInternetConnection()) {
                    AppError.SlowConnection
                } else {
                    AppError.NoInternet
                }
            }
            
            else -> AppError.Unknown(exception)
        }
    }
    
    /**
     * Execute API call with retry logic and proper error handling
     */
    suspend fun <T> executeWithRetry(
        retryConfig: RetryConfig = RetryConfig(),
        apiCall: suspend () -> T
    ): ApiResult<T> {
        var currentDelay = retryConfig.initialDelayMs
        var lastError: AppError? = null
        
        repeat(retryConfig.maxAttempts) { attempt ->
            try {
                // Check internet connection before making request
                if (!hasInternetConnection()) {
                    return ApiResult.Error(AppError.NoInternet)
                }
                
                val result = apiCall()
                return ApiResult.Success(result)
                
            } catch (e: Exception) {
                val error = handleException(e)
                lastError = error
                
                // Don't retry if error is not retryable or this is the last attempt
                if (!error.isRetryable || attempt == retryConfig.maxAttempts - 1) {
                    return ApiResult.Error(error)
                }
                
                // Wait before retrying (exponential backoff)
                delay(currentDelay)
                currentDelay = minOf(
                    (currentDelay * retryConfig.backoffMultiplier).toLong(),
                    retryConfig.maxDelayMs
                )
            }
        }
        
        return ApiResult.Error(lastError ?: AppError.Unknown(Exception("Max retry attempts reached")))
    }
    
    /**
     * Get timeout values based on connection quality
     */
    fun getTimeoutConfig(): TimeoutConfig {
        return when (getConnectionQuality()) {
            ConnectionQuality.WIFI -> TimeoutConfig(
                connectTimeoutMs = 10_000L,  // 10 seconds
                readTimeoutMs = 30_000L,     // 30 seconds
                writeTimeoutMs = 15_000L     // 15 seconds
            )
            ConnectionQuality.CELLULAR_GOOD -> TimeoutConfig(
                connectTimeoutMs = 15_000L,  // 15 seconds
                readTimeoutMs = 45_000L,     // 45 seconds
                writeTimeoutMs = 20_000L     // 20 seconds
            )
            ConnectionQuality.CELLULAR_POOR, ConnectionQuality.POOR -> TimeoutConfig(
                connectTimeoutMs = 20_000L,  // 20 seconds
                readTimeoutMs = 60_000L,     // 60 seconds
                writeTimeoutMs = 30_000L     // 30 seconds
            )
            ConnectionQuality.NONE -> TimeoutConfig(
                connectTimeoutMs = 5_000L,   // Fail fast
                readTimeoutMs = 5_000L,
                writeTimeoutMs = 5_000L
            )
        }
    }
}

// Connection quality enum
enum class ConnectionQuality {
    NONE,
    POOR,
    CELLULAR_POOR,
    CELLULAR_GOOD,
    WIFI
}

// Timeout configuration
data class TimeoutConfig(
    val connectTimeoutMs: Long,
    val readTimeoutMs: Long,
    val writeTimeoutMs: Long
)

// Extension functions for easier error handling in ViewModels
suspend fun <T> safeApiCall(
    errorHandler: ErrorHandler,
    retryConfig: RetryConfig = RetryConfig(),
    apiCall: suspend () -> T
): ApiResult<T> {
    return errorHandler.executeWithRetry(retryConfig, apiCall)
}

/**
 * User-friendly error messages for specific scenarios
 */
object ErrorMessages {
    fun getRoutingErrorMessage(provider: String): String {
        return when (provider.lowercase()) {
            "google" -> "Unable to find route using Google Maps. Trying alternative route service..."
            "here" -> "HERE routing service unavailable. Trying alternative service..."
            "mapbox" -> "Mapbox routing failed. Trying backup service..."
            else -> "Route calculation failed. Using fallback route."
        }
    }
    
    fun getLocationErrorMessage(type: String): String {
        return when (type.lowercase()) {
            "permission" -> "Location permission is required to show your current position"
            "unavailable" -> "Unable to get your location. Please check your location settings"
            "timeout" -> "Location request timed out. Using default location"
            else -> "Location service error"
        }
    }
    
    fun getDriverSearchErrorMessage(): String {
        return "No drivers available at the moment. Please try again in a few minutes"
    }
}
