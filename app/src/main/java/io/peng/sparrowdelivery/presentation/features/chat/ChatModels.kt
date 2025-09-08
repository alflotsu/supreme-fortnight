package io.peng.sparrowdelivery.presentation.features.chat

import java.text.SimpleDateFormat
import java.util.*

enum class MessageSender {
    CUSTOMER,
    DRIVER
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

enum class MessageType {
    TEXT,
    LOCATION,
    IMAGE,
    SYSTEM_NOTIFICATION
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val type: MessageType = MessageType.TEXT,
    val isRead: Boolean = false
) {
    fun getFormattedTime(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(Date(timestamp))
    }
    
    fun getFormattedDate(): String {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(Date(timestamp))
    }
    
    fun isToday(): Boolean {
        val today = Calendar.getInstance()
        val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR)
    }
    
    fun isYesterday(): Boolean {
        val yesterday = Calendar.getInstance().apply { 
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return yesterday.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR)
    }
}

data class ChatInfo(
    val driverName: String,
    val driverPhone: String,
    val driverVehicle: String,
    val driverPlateNumber: String,
    val isDriverOnline: Boolean = true,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
) {
    fun getLastSeenText(): String {
        if (isDriverOnline) return "Online"
        
        val now = System.currentTimeMillis()
        val diff = now - lastSeenTimestamp
        
        return when {
            diff < 60_000 -> "Last seen just now"
            diff < 3600_000 -> "Last seen ${diff / 60_000} min ago"
            diff < 86400_000 -> "Last seen ${diff / 3600_000}h ago"
            else -> "Last seen ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(lastSeenTimestamp))}"
        }
    }
}
