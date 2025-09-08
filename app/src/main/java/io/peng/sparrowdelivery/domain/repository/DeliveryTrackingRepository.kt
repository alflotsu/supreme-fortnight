package io.peng.sparrowdelivery.domain.repository

import io.peng.sparrowdelivery.data.models.DeliveryTracking
import kotlinx.coroutines.flow.Flow

interface DeliveryTrackingRepository {
    suspend fun getDeliveryByTrackingCode(trackingCode: String): Result<DeliveryTracking?>
    suspend fun getAllActiveDeliveries(): Result<List<DeliveryTracking>>
    suspend fun getDeliveryById(deliveryId: String): Result<DeliveryTracking?>
    suspend fun subscribeToDeliveryUpdates(trackingCode: String): Flow<DeliveryTracking?>
    suspend fun createDelivery(delivery: DeliveryTracking): Result<DeliveryTracking>
    suspend fun updateDeliveryStatus(trackingCode: String, status: io.peng.sparrowdelivery.data.models.DeliveryTrackingStatus): Result<DeliveryTracking>
    suspend fun updateDriverLocation(trackingCode: String, latitude: Double, longitude: Double): Result<Unit>
    suspend fun addTrackingEvent(trackingCode: String, event: io.peng.sparrowdelivery.data.models.TrackingEvent): Result<Unit>
}
