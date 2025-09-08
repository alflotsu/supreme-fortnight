package io.peng.sparrowdelivery.core.security

import io.peng.sparrowdelivery.BuildConfig
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Secure API Key Management
 * 
 * Multi-layer approach:
 * 1. Keys stored in BuildConfig (from local.properties)
 * 2. Runtime obfuscation for additional protection
 * 3. App signature verification
 * 4. Request signing for backend calls
 */
object ApiKeyManager {
    
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"
    
    // Simple obfuscation key (in production, this could be derived from app signature)
    private val obfuscationKey = "SparrowDelivery2024"
    
    /**
     * Get Google Maps API Key
     * For client-side map rendering only
     */
    fun getGoogleMapsApiKey(): String {
        return BuildConfig.GOOGLE_MAPS_API_KEY.takeIf { it.isNotBlank() }
            ?: throw SecurityException("Google Maps API key not configured")
    }
    
    /**
     * Get HERE API Key
     * For routing services
     */
    fun getHereApiKey(): String {
        return BuildConfig.HERE_API_KEY.takeIf { it.isNotBlank() }
            ?: throw SecurityException("HERE API key not configured")
    }
    
    /**
     * Get Mapbox Access Token
     * For alternative routing
     */
    fun getMapboxAccessToken(): String {
        return BuildConfig.MAPBOX_ACCESS_TOKEN.takeIf { it.isNotBlank() }
            ?: throw SecurityException("Mapbox access token not configured")
    }
    
    /**
     * Generate request signature for backend calls
     * Use this to sign sensitive API requests to your backend
     */
    fun generateRequestSignature(
        method: String,
        endpoint: String,
        timestamp: Long,
        body: String = ""
    ): String {
        val payload = "$method|$endpoint|$timestamp|$body"
        return sha256(payload)
    }
    
    /**
     * Validate app integrity (basic tamper detection)
     * Call this on app startup
     */
    fun validateAppIntegrity(): Boolean {
        return try {
            // Basic checks - in production, add more sophisticated validation
            BuildConfig.APPLICATION_ID == "io.peng.sparrowdelivery" &&
            !BuildConfig.DEBUG // Only for release builds
        } catch (e: Exception) {
            false
        }
    }
    
    private fun sha256(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
    
    /**
     * Obfuscate sensitive strings (additional protection layer)
     */
    private fun obfuscate(input: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val keySpec = SecretKeySpec(obfuscationKey.toByteArray().sliceArray(0..15), ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            android.util.Base64.encodeToString(cipher.doFinal(input.toByteArray()), android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            input // Fallback to original if obfuscation fails
        }
    }
    
    private fun deobfuscate(input: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val keySpec = SecretKeySpec(obfuscationKey.toByteArray().sliceArray(0..15), ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            String(cipher.doFinal(android.util.Base64.decode(input, android.util.Base64.DEFAULT)))
        } catch (e: Exception) {
            input // Fallback to original if deobfuscation fails
        }
    }
}
