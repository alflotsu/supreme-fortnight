package io.peng.sparrowdelivery.data.utils

import com.google.android.gms.maps.model.LatLng
import io.peng.sparrowdelivery.domain.entities.*
import kotlin.math.round

/**
 * Polyline conversion utilities for Supreme Fortnight delivery app
 * Simplified to handle only Google Maps polyline encoding/decoding
 */
object PolylineConverter {

    /**
     * Convert Google Maps API response to unified Route domain entity
     */
    fun toRoute(response: Any, provider: RouteProvider): Route? {
        return when (provider) {
            RouteProvider.GOOGLE_MAPS -> convertGoogleResponse(response as GoogleRouteResponse)
            else -> {
                android.util.Log.w("PolylineConverter", "Only Google Maps is supported. Provider: $provider")
                null
            }
        }
    }


    /**
     * Convert Google Maps response to unified Route
     */
    private fun convertGoogleResponse(response: GoogleRouteResponse): Route? {
        val route = response.routes.firstOrNull() ?: return null
        val leg = route.legs.firstOrNull() ?: return null
        
        val coordinates = decodeGooglePolyline(route.overview_polyline.points)
        
        return Route(
            coordinates = coordinates,
            encodedPolyline = route.overview_polyline.points,
            distanceMeters = leg.distance.value.toDouble(),
            durationSeconds = leg.duration.value,
            summary = RouteSummary(
                startAddress = leg.start_address,
                endAddress = leg.end_address,
                instructions = emptyList(),
                trafficEnabled = false
            ),
            provider = RouteProvider.GOOGLE_MAPS
        )
    }

    /**
     * Decode Google/Mapbox polyline format to coordinates
     * This is the standard algorithm used by Google Maps and Mapbox
     */
    fun decodeGooglePolyline(encoded: String): List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        var lat = 0
        var lng = 0
        var index = 0
        
        while (index < encoded.length) {
            // Decode latitude
            var shift = 0
            var result = 0
            var b: Int
            
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val deltaLat = if ((result and 1) != 0) -(result shr 1) else (result shr 1)
            lat += deltaLat
            
            // Decode longitude  
            shift = 0
            result = 0
            
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val deltaLng = if ((result and 1) != 0) -(result shr 1) else (result shr 1)
            lng += deltaLng
            
            coordinates.add(LatLng(lat / 1e5, lng / 1e5))
        }
        
        return coordinates
    }

    /**
     * Encode coordinates to Google polyline format
     */
    fun encodeToGooglePolyline(coordinates: List<LatLng>): String {
        if (coordinates.isEmpty()) return ""
        
        var lat = 0
        var lng = 0
        val result = StringBuilder()
        
        for (coordinate in coordinates) {
            val latE5 = round(coordinate.latitude * 1e5).toInt()
            val lngE5 = round(coordinate.longitude * 1e5).toInt()
            
            val deltaLat = latE5 - lat
            val deltaLng = lngE5 - lng
            
            lat = latE5
            lng = lngE5
            
            result.append(encodeSignedNumber(deltaLat))
            result.append(encodeSignedNumber(deltaLng))
        }
        
        return result.toString()
    }

    private fun encodeSignedNumber(num: Int): String {
        var signedNumber = num shl 1
        if (num < 0) {
            signedNumber = signedNumber.inv()
        }
        return encodeNumber(signedNumber)
    }

    private fun encodeNumber(num: Int): String {
        var number = num
        val encoded = StringBuilder()
        
        while (number >= 0x20) {
            encoded.append(((0x20 or (number and 0x1f)) + 63).toChar())
            number = number shr 5
        }
        encoded.append((number + 63).toChar())
        
        return encoded.toString()
    }

    /**
     * Convert coordinates to Google polyline format (for Google Maps display)
     */
    private fun convertToGooglePolyline(coordinates: List<LatLng>): String {
        return encodeToGooglePolyline(coordinates)
    }


    /**
     * Ghana-specific coordinate validation
     * Ensures coordinates are within Ghana's boundaries for NoHike deliveries
     */
    fun isValidGhanaCoordinate(coordinate: LatLng): Boolean {
        // Ghana approximate boundaries
        val minLat = 4.5 // Southern boundary
        val maxLat = 11.5 // Northern boundary  
        val minLng = -3.5 // Western boundary
        val maxLng = 1.5 // Eastern boundary
        
        return coordinate.latitude in minLat..maxLat && 
               coordinate.longitude in minLng..maxLng
    }

    /**
     * KNUST campus coordinate validation (for campus delivery testing)
     */
    fun isKnustCampusArea(coordinate: LatLng): Boolean {
        // KNUST campus approximate boundaries
        val campusCenter = LatLng(6.6745, -1.5716)
        val campusRadiusKm = 3.0
        
        return distanceBetween(coordinate, campusCenter) <= campusRadiusKm
    }

    /**
     * Calculate distance between two coordinates (in kilometers)
     */
    private fun distanceBetween(coord1: LatLng, coord2: LatLng): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(coord2.latitude - coord1.latitude)
        val dLon = Math.toRadians(coord2.longitude - coord1.longitude)
        val lat1 = Math.toRadians(coord1.latitude)
        val lat2 = Math.toRadians(coord2.latitude)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2) * 
                kotlin.math.cos(lat1) * kotlin.math.cos(lat2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadiusKm * c
    }
}

/**
 * Extension functions for easy conversion
 */
fun GoogleRouteResponse.toRoute(): Route? = PolylineConverter.toRoute(this, RouteProvider.GOOGLE_MAPS)
