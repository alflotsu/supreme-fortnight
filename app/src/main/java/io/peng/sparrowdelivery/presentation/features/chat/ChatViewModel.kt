package io.peng.sparrowdelivery.presentation.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val chatInfo: ChatInfo? = null,
    val isLoading: Boolean = false,
    val currentMessageText: String = "",
    val isTyping: Boolean = false,
    val isSendingMessage: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    // Simulated chat storage
    private val messageStorage = mutableListOf<ChatMessage>()
    
    fun initializeChat(driverName: String, driverPhone: String, driverVehicle: String, driverPlateNumber: String) {
        val chatInfo = ChatInfo(
            driverName = driverName,
            driverPhone = driverPhone,
            driverVehicle = driverVehicle,
            driverPlateNumber = driverPlateNumber,
            isDriverOnline = true
        )
        
        _uiState.update { state ->
            state.copy(
                chatInfo = chatInfo,
                isLoading = true
            )
        }
        
        // Load initial mock messages
        loadMockMessages()
    }
    
    private fun loadMockMessages() {
        viewModelScope.launch {
            // Simulate loading delay
            delay(1000)
            
            val mockMessages = listOf(
                ChatMessage(
                    text = "Hello! I'm Kwame, your delivery driver. I'm on my way to pick up your package 👋",
                    sender = MessageSender.DRIVER,
                    timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    text = "Hi Kwame! Thank you. How long will it take approximately?",
                    sender = MessageSender.CUSTOMER,
                    timestamp = System.currentTimeMillis() - 1740000, // 29 minutes ago
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    text = "I'll be there in about 5-8 minutes. Traffic is light today 🚗",
                    sender = MessageSender.DRIVER,
                    timestamp = System.currentTimeMillis() - 1680000, // 28 minutes ago
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    text = "Perfect! I'll be waiting outside. The building has a blue gate.",
                    sender = MessageSender.CUSTOMER,
                    timestamp = System.currentTimeMillis() - 1620000, // 27 minutes ago
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    text = "Got it! Blue gate. I'll call when I arrive 📞",
                    sender = MessageSender.DRIVER,
                    timestamp = System.currentTimeMillis() - 1560000, // 26 minutes ago
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    text = "Package picked up successfully! Now heading to delivery location 📦✅",
                    sender = MessageSender.DRIVER,
                    timestamp = System.currentTimeMillis() - 900000, // 15 minutes ago
                    status = MessageStatus.READ,
                    type = MessageType.SYSTEM_NOTIFICATION
                ),
                ChatMessage(
                    text = "Great! Thank you for the update 😊",
                    sender = MessageSender.CUSTOMER,
                    timestamp = System.currentTimeMillis() - 840000, // 14 minutes ago
                    status = MessageStatus.READ
                )
            )
            
            messageStorage.addAll(mockMessages)
            
            _uiState.update { state ->
                state.copy(
                    messages = messageStorage.toList(),
                    isLoading = false
                )
            }
        }
    }
    
    fun updateMessageText(text: String) {
        _uiState.update { it.copy(currentMessageText = text) }
    }
    
    fun sendMessage() {
        val messageText = _uiState.value.currentMessageText.trim()
        if (messageText.isEmpty() || _uiState.value.isSendingMessage) return
        
        _uiState.update { it.copy(isSendingMessage = true) }
        
        viewModelScope.launch {
            try {
                // Create the message
                val message = ChatMessage(
                    text = messageText,
                    sender = MessageSender.CUSTOMER,
                    status = MessageStatus.SENDING
                )
                
                // Add to local storage
                messageStorage.add(message)
                
                // Update UI immediately
                _uiState.update { state ->
                    state.copy(
                        messages = messageStorage.toList(),
                        currentMessageText = "",
                        isSendingMessage = false
                    )
                }
                
                // Simulate network delay for sending
                delay(1500)
                
                // Update message status to sent
                val sentMessage = message.copy(status = MessageStatus.SENT)
                val messageIndex = messageStorage.indexOfFirst { it.id == message.id }
                if (messageIndex >= 0) {
                    messageStorage[messageIndex] = sentMessage
                }
                
                _uiState.update { state ->
                    state.copy(messages = messageStorage.toList())
                }
                
                // Simulate driver response (20% chance)
                if (kotlin.random.Random.nextFloat() < 0.2f) {
                    simulateDriverResponse()
                }
                
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isSendingMessage = false,
                        errorMessage = "Failed to send message: ${exception.message}"
                    )
                }
            }
        }
    }
    
    private fun simulateDriverResponse() {
        viewModelScope.launch {
            // Show typing indicator
            _uiState.update { it.copy(isTyping = true) }
            
            delay(2000) // Typing delay
            
            val responses = listOf(
                "Thanks for the message! 👍",
                "I'll keep you updated!",
                "Almost there! 🚗💨",
                "Copy that! 📝",
                "No problem at all! 😊",
                "ETA in 3-5 minutes ⏰"
            )
            
            val response = ChatMessage(
                text = responses.random(),
                sender = MessageSender.DRIVER,
                status = MessageStatus.SENT
            )
            
            messageStorage.add(response)
            
            _uiState.update { state ->
                state.copy(
                    messages = messageStorage.toList(),
                    isTyping = false
                )
            }
        }
    }
    
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun markMessagesAsRead() {
        viewModelScope.launch {
            val updatedMessages = messageStorage.map { message ->
                if (message.sender == MessageSender.DRIVER && !message.isRead) {
                    message.copy(isRead = true)
                } else {
                    message
                }
            }
            
            messageStorage.clear()
            messageStorage.addAll(updatedMessages)
            
            _uiState.update { state ->
                state.copy(messages = messageStorage.toList())
            }
        }
    }
    
    fun simulateDriverOffline() {
        _uiState.update { state ->
            state.copy(
                chatInfo = state.chatInfo?.copy(
                    isDriverOnline = false,
                    lastSeenTimestamp = System.currentTimeMillis() - 300000 // 5 minutes ago
                )
            )
        }
    }
    
    fun simulateDriverOnline() {
        _uiState.update { state ->
            state.copy(
                chatInfo = state.chatInfo?.copy(
                    isDriverOnline = true,
                    lastSeenTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
