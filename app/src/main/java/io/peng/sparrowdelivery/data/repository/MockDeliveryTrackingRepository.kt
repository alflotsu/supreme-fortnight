package io.peng.sparrowdelivery.data.repository

import io.peng.sparrowdelivery.data.models.DeliveryTracking
import io.peng.sparrowdelivery.data.models.DeliveryTrackingStatus
import io.peng.sparrowdelivery.data.models.TrackingEvent
import io.peng.sparrowdelivery.domain.repository.DeliveryTrackingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MockDeliveryTrackingRepository : DeliveryTrackingRepository {
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val currentTime = dateFormatter.format(Date())
    
    // Mock data with working tracking codes
    private val mockDeliveries = listOf(
        createMockDelivery("ABCD-1234", DeliveryTrackingStatus.EN_ROUTE_TO_DELIVERY, "Kwame Asante"),
        createMockDelivery("EFGH-5678", DeliveryTrackingStatus.ITEM_PICKED_UP, "Akosua Mensah"),
        createMockDelivery("IJKL-9012", DeliveryTrackingStatus.DRIVER_ASSIGNED, "Kofi Boateng"),
        createMockDelivery("MNOP-3456", DeliveryTrackingStatus.DRIVER_EN_ROUTE_TO_PICKUP, "Ama Osei"),
        createMockDelivery("QRST-7890", DeliveryTrackingStatus.DELIVERED, "Yaw Darko")
    )
    
    override suspend fun getDeliveryByTrackingCode(trackingCode: String): Result<DeliveryTracking?> {
        // Simulate API delay
        delay(1000)
        
        val delivery = mockDeliveries.find { it.trackingCode == trackingCode }
        return Result.success(delivery)
    }
    
    override suspend fun getAllActiveDeliveries(): Result<List<DeliveryTracking>> {
        delay(500)
        
        val activeDeliveries = mockDeliveries.filter { 
            it.status != DeliveryTrackingStatus.DELIVERED && 
            it.status != DeliveryTrackingStatus.CANCELLED 
        }
        return Result.success(activeDeliveries)
    }
    
    override suspend fun getDeliveryById(deliveryId: String): Result<DeliveryTracking?> {
        delay(500)
        val delivery = mockDeliveries.find { it.id == deliveryId }
        return Result.success(delivery)
    }
    
    override suspend fun subscribeToDeliveryUpdates(trackingCode: String): Flow<DeliveryTracking?> {
        return flow {
            val delivery = mockDeliveries.find { it.trackingCode == trackingCode }
            emit(delivery)
            
            // Simulate real-time updates every 10 seconds
            while (true) {
                delay(10000)
                emit(delivery)
            }
        }
    }
    
    override suspend fun createDelivery(delivery: DeliveryTracking): Result<DeliveryTracking> {
        delay(500)
        return Result.success(delivery)
    }
    
    override suspend fun updateDeliveryStatus(
        trackingCode: String, 
        status: DeliveryTrackingStatus
    ): Result<DeliveryTracking> {
        delay(500)
        val delivery = mockDeliveries.find { it.trackingCode == trackingCode }
        return if (delivery != null) {
            val updatedDelivery = delivery.copy(status = status)
            Result.success(updatedDelivery)
        } else {
            Result.failure(Exception("Delivery not found"))
        }
    }
    
    override suspend fun updateDriverLocation(
        trackingCode: String, 
        latitude: Double, 
        longitude: Double
    ): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
    
    override suspend fun addTrackingEvent(
        trackingCode: String, 
        event: TrackingEvent
    ): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
    
    private fun createMockDelivery(
        trackingCode: String, 
        status: DeliveryTrackingStatus,
        driverName: String
    ): DeliveryTracking {
        return DeliveryTracking(
            id = java.util.UUID.randomUUID().toString(),
            trackingCode = trackingCode,
            status = status,
            pickupLocation = io.peng.sparrowdelivery.data.models.TrackingLocation(
                address = "Accra Mall, Tetteh Quarshie Roundabout",
                latitude = 5.6037,
                longitude = -0.1870,
                contactName = "Shop Owner",
                contactPhone = "+233-24-111-2222"
            ),
            dropoffLocation = io.peng.sparrowdelivery.data.models.TrackingLocation(
                address = "University of Ghana, Legon Campus",
                latitude = 5.6515,
                longitude = -0.1870,
                contactName = "John Doe",
                contactPhone = "+233-24-333-4444"
            ),
            driverInfo = io.peng.sparrowdelivery.data.models.DriverTrackingInfo(
                id = java.util.UUID.randomUUID().toString(),
                name = driverName,
                phone = "+233-24-123-4567",
                vehicleType = getRandomVehicle(),
                plateNumber = generatePlateNumber(),
                rating = (4.2f + Math.random() * 0.8f).toFloat()
            ),
            customerInfo = io.peng.sparrowdelivery.data.models.CustomerTrackingInfo(
                name = "John Doe",
                phone = "+233-24-333-4444",
                email = "john.doe@email.com"
            ),
            estimatedArrivalTime = getEstimatedArrival(),
            createdAt = currentTime,
            updatedAt = currentTime,
            timeline = createMockTimeline(status),
            notes = if (status == DeliveryTrackingStatus.DELIVERED) "Package delivered successfully" else null
        )
    }
    
    private fun createMockTimeline(currentStatus: DeliveryTrackingStatus): List<TrackingEvent> {
        val timeline = mutableListOf<TrackingEvent>()
        val statuses = DeliveryTrackingStatus.values()
        val currentIndex = statuses.indexOf(currentStatus)
        
        for (i in 0..currentIndex) {
            val status = statuses[i]
            val timeOffset = i * 15 * 60 * 1000 // 15 minutes apart
            val eventTime = Date(System.currentTimeMillis() - (currentIndex - i) * timeOffset)
            
            timeline.add(
                TrackingEvent(
                    id = java.util.UUID.randomUUID().toString(),
                    status = status,
                    timestamp = dateFormatter.format(eventTime),
                    description = getStatusDescription(status),
                    location = null
                )
            )
        }
        
        return timeline
    }
    
    private fun getStatusDescription(status: DeliveryTrackingStatus): String {
        return when (status) {
            DeliveryTrackingStatus.CREATED -> "Your delivery request has been created and is being processed"
            DeliveryTrackingStatus.DRIVER_ASSIGNED -> "A driver has been assigned to handle your delivery"
            DeliveryTrackingStatus.DRIVER_EN_ROUTE_TO_PICKUP -> "Driver is on the way to pickup location"
            DeliveryTrackingStatus.DRIVER_ARRIVED_AT_PICKUP -> "Driver has arrived at the pickup location"
            DeliveryTrackingStatus.ITEM_PICKED_UP -> "Your item has been picked up and is ready for delivery"
            DeliveryTrackingStatus.EN_ROUTE_TO_DELIVERY -> "Driver is heading to your delivery location"
            DeliveryTrackingStatus.DRIVER_ARRIVED_AT_DROPOFF -> "Driver has arrived at delivery location"
            DeliveryTrackingStatus.DELIVERED -> "Your package has been delivered successfully"
            DeliveryTrackingStatus.CANCELLED -> "Delivery has been cancelled"
            DeliveryTrackingStatus.FAILED -> "Delivery attempt failed"
        }
    }
    
    private fun getRandomVehicle(): String {
        val vehicles = listOf("Toyota Camry", "Honda Accord", "Nissan Sentra", "Hyundai Elantra", "Kia Cerato")
        return vehicles.random()
    }
    
    private fun generatePlateNumber(): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        return "GR-${numbers.random()}${numbers.random()}${numbers.random()}${numbers.random()}-${numbers.random()}${numbers.random()}"
    }
    
    private fun getEstimatedArrival(): String {
        val futureTime = Date(System.currentTimeMillis() + (15 + Math.random() * 30).toLong() * 60 * 1000)
        return dateFormatter.format(futureTime)
    }
}
