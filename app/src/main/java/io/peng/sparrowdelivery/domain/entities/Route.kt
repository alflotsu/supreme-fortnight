package io.peng.sparrowdelivery.domain.entities

import com.google.android.gms.maps.model.LatLng

/**
 * Domain entity for route information
 * Unified format regardless of the underlying API (HERE, Mapbox, Google)
 */
data class Route(
    val coordinates: List<LatLng>,
    val encodedPolyline: String,
    val distanceMeters: Double,
    val durationSeconds: Int,
    val summary: RouteSummary,
    val provider: RouteProvider
) {
    val distanceKilometers: Double get() = distanceMeters / 1000.0
    val durationMinutes: Int get() = durationSeconds / 60
}

data class RouteSummary(
    val startAddress: String? = null,
    val endAddress: String? = null,
    val instructions: List<RouteInstruction> = emptyList(),
    val trafficEnabled: Boolean = false
)

data class RouteInstruction(
    val text: String,
    val distanceMeters: Double,
    val durationSeconds: Int
)

enum class RouteProvider {
    GOOGLE_MAPS
}

/**
 * Request parameters for route calculation
 */
data class RouteRequest(
    val origin: LatLng,
    val destination: LatLng,
    val transportMode: TransportMode = TransportMode.CAR,
    val avoidTolls: Boolean = false,
    val useTraffic: Boolean = false,
    val waypoints: List<LatLng> = emptyList()
)

enum class TransportMode {
    CAR,
    BICYCLE,
    WALKING,
    MOTORCYCLE
}

/**
 * HERE Maps API Response DTOs
 */
data class HereRouteResponse(
    val routes: List<HereRoute>
)

data class HereRoute(
    val sections: List<HereSection>
)

data class HereSection(
    val polyline: String, // Flexible polyline encoding
    val summary: HereSummary,
    val departure: HereWaypoint,
    val arrival: HereWaypoint
)

data class HereSummary(
    val length: Int, // meters
    val duration: Int, // seconds
    val baseDuration: Int? = null
)

data class HereWaypoint(
    val place: HerePlace
)

data class HerePlace(
    val location: HereLocation
)

data class HereLocation(
    val lat: Double,
    val lng: Double
)

/**
 * Mapbox API Response DTOs
 */
data class MapboxRouteResponse(
    val routes: List<MapboxRoute>,
    val code: String
)

data class MapboxRoute(
    val geometry: String, // Encoded polyline (Google format)
    val legs: List<MapboxLeg>,
    val distance: Double, // meters
    val duration: Double, // seconds
    val weight_name: String? = null,
    val weight: Double? = null
)

data class MapboxLeg(
    val steps: List<MapboxStep>? = null,
    val summary: String? = null,
    val distance: Double,
    val duration: Double
)

data class MapboxStep(
    val intersections: List<MapboxIntersection>? = null,
    val maneuver: MapboxManeuver? = null,
    val name: String? = null,
    val duration: Double,
    val distance: Double,
    val geometry: String? = null
)

data class MapboxIntersection(
    val location: List<Double>, // [longitude, latitude]
    val bearings: List<Int>? = null,
    val entry: List<Boolean>? = null
)

data class MapboxManeuver(
    val location: List<Double>, // [longitude, latitude]
    val bearing_before: Int? = null,
    val bearing_after: Int? = null,
    val instruction: String? = null,
    val type: String? = null,
    val modifier: String? = null
)

/**
 * Google Maps API Response DTOs (for reference/fallback)
 */
data class GoogleRouteResponse(
    val routes: List<GoogleRoute>,
    val status: String
)

data class GoogleRoute(
    val overview_polyline: GooglePolyline,
    val legs: List<GoogleLeg>,
    val summary: String? = null,
    val warnings: List<String>? = null
)

data class GooglePolyline(
    val points: String // Encoded polyline
)

data class GoogleLeg(
    val distance: GoogleDistance,
    val duration: GoogleDuration,
    val end_address: String,
    val start_address: String,
    val steps: List<GoogleStep>? = null
)

data class GoogleDistance(
    val text: String,
    val value: Int // meters
)

data class GoogleDuration(
    val text: String,
    val value: Int // seconds
)

data class GoogleStep(
    val distance: GoogleDistance,
    val duration: GoogleDuration,
    val end_location: GoogleLocation,
    val html_instructions: String,
    val polyline: GooglePolyline,
    val start_location: GoogleLocation,
    val travel_mode: String
)

data class GoogleLocation(
    val lat: Double,
    val lng: Double
)
