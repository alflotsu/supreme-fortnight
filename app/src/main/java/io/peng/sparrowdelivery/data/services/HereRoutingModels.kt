package io.peng.sparrowdelivery.data.services

import com.google.gson.annotations.SerializedName
import io.peng.sparrowdelivery.presentation.features.home.LocationCoordinate

// HERE API Response Models
data class HereRoutingResponse(
    @SerializedName("routes")
    val routes: List<HereRoute>
)

data class HereRoute(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("sections")
    val sections: List<HereSection>
)

data class HereSection(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("departure")
    val departure: HereWaypoint,
    
    @SerializedName("arrival")
    val arrival: HereWaypoint,
    
    @SerializedName("summary")
    val summary: HereSummary,
    
    @SerializedName("polyline")
    val polyline: String,
    
    @SerializedName("transport")
    val transport: HereTransport?
)

data class HereWaypoint(
    @SerializedName("time")
    val time: String,
    
    @SerializedName("place")
    val place: HerePlace
)

data class HerePlace(
    @SerializedName("type")
    val type: String,
    
    @SerializedName("location")
    val location: HereLocation,
    
    @SerializedName("originalLocation")
    val originalLocation: HereLocation?
)

data class HereLocation(
    @SerializedName("lat")
    val lat: Double,
    
    @SerializedName("lng")
    val lng: Double
)

data class HereSummary(
    @SerializedName("duration")
    val duration: Int, // in seconds
    
    @SerializedName("length")
    val length: Int, // in meters
    
    @SerializedName("baseDuration")
    val baseDuration: Int?,
    
    @SerializedName("trafficDelay")
    val trafficDelay: Int?
)

data class HereTransport(
    @SerializedName("mode")
    val mode: String
)

// Domain Models for the App
data class RouteInfo(
    val id: String,
    val polylinePoints: List<LocationCoordinate>,
    val distance: Int, // meters
    val duration: Int, // seconds  
    val trafficDelay: Int?, // seconds
    val routeType: RouteType,
    val summary: String
) {
    val distanceText: String
        get() = when {
            distance < 1000 -> "${distance}m"
            distance < 10000 -> "${"%.1f".format(distance / 1000.0)}km"
            else -> "${(distance / 1000)}km"
        }
    
    val durationText: String
        get() {
            val minutes = (duration + 30) / 60 // Round to nearest minute
            return when {
                minutes < 60 -> "${minutes}min"
                else -> {
                    val hours = minutes / 60
                    val remainingMinutes = minutes % 60
                    if (remainingMinutes == 0) "${hours}h"
                    else "${hours}h ${remainingMinutes}min"
                }
            }
        }
    
    val hasTraffic: Boolean
        get() = trafficDelay != null && trafficDelay > 0
    
    val trafficDelayText: String?
        get() = trafficDelay?.let { delay ->
            val delayMinutes = (delay + 30) / 60
            if (delayMinutes > 0) "+${delayMinutes}min traffic" else null
        }
}

enum class RouteType(val displayName: String, val description: String) {
    FAST("Fast", "Fastest route with current traffic"),
    SHORT("Short", "Shortest distance"),
    BALANCED("Balanced", "Balance of time and distance"),
    ECONOMIC("Economic", "Most fuel efficient")
}

data class RouteRequest(
    val origin: LocationCoordinate,
    val destination: LocationCoordinate,
    val waypoints: List<LocationCoordinate> = emptyList(),
    val routeTypes: List<RouteType> = listOf(RouteType.FAST),
    val transportMode: String = "car",
    val departureTime: String? = null // ISO 8601 format for scheduled deliveries
) {
    fun toQueryMap(): Map<String, String> {
        val params = mutableMapOf<String, String>()
        
        // Origin and destination
        params["origin"] = "${origin.latitude},${origin.longitude}"
        params["destination"] = "${destination.latitude},${destination.longitude}"
        
        // Waypoints (intermediate stops)
        if (waypoints.isNotEmpty()) {
            val waypointString = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
            params["via"] = waypointString
        }
        
        // Transport mode
        params["transportMode"] = transportMode
        
        // Return options
        params["return"] = "polyline,summary,typicalDuration,instructions"
        
        // Route types/alternatives
        if (routeTypes.size > 1) {
            params["alternatives"] = (routeTypes.size - 1).toString()
        }
        
        // Departure time for scheduled routes
        departureTime?.let { params["departureTime"] = it }
        
        // Ghana-specific optimizations
        params["spans"] = "countryCode,speedLimit"
        
        return params
    }
}
