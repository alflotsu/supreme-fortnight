package io.peng.sparrowdelivery.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar

data class UserProfile(
    val id: String = "user_123",
    val name: String = "John Doe",
    val email: String = "john.doe@example.com",
    val phone: String = "+1 234 567 8900",
    val profileImageUrl: String = "",
    val address: String = "123 Main St, San Francisco, CA",
    val memberSince: String = "January 2024",
    val totalDeliveries: Int = 47,
    val savedAddresses: List<SavedAddress> = emptyList(),
    val preferredPaymentMethod: String = "**** **** **** 1234",
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val deliveryPreferences: DeliveryPreferences = DeliveryPreferences(),
    val orderHistory: List<Order> = emptyList()
)

data class SavedAddress(
    val id: String,
    val label: String, // "Home", "Work", "Mom's House"
    val address: String,
    val isDefault: Boolean = false
)

data class DeliveryPreferences(
    val defaultTransportMode: String = "Compact Car",
    val allowContactRecipient: Boolean = true,
    val defaultDeliveryTime: String = "Standard",
    val requireSignature: Boolean = false,
    val notifications: NotificationPreferences = NotificationPreferences()
)

data class NotificationPreferences(
    val orderUpdates: Boolean = true,
    val promotions: Boolean = true,
    val driverLocation: Boolean = true,
    val deliveryConfirmation: Boolean = true
)

enum class PaymentMethodType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    APPLE_PAY,
    GOOGLE_PAY,
    BANK_ACCOUNT
}

data class PaymentMethod(
    val id: String,
    val type: PaymentMethodType,
    val displayName: String,
    val lastFourDigits: String? = null,
    val expiryDate: String? = null,
    val isDefault: Boolean = false,
    val iconResource: String? = null
)

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showPaymentMethodsDialog: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Simulate loading user data
        val sampleAddresses = listOf(
            SavedAddress("1", "Home", "123 Main St, San Francisco, CA", isDefault = true),
            SavedAddress("2", "Work", "456 Tech Ave, San Francisco, CA"),
            SavedAddress("3", "Mom's House", "789 Family Rd, Oakland, CA")
        )
        
        // Sample payment methods
        val samplePaymentMethods = listOf(
            PaymentMethod(
                id = "pm_001",
                type = PaymentMethodType.CREDIT_CARD,
                displayName = "Visa ending in 1234",
                lastFourDigits = "1234",
                expiryDate = "12/27",
                isDefault = true
            ),
            PaymentMethod(
                id = "pm_002",
                type = PaymentMethodType.CREDIT_CARD,
                displayName = "Mastercard ending in 5678",
                lastFourDigits = "5678",
                expiryDate = "08/26",
                isDefault = false
            ),
            PaymentMethod(
                id = "pm_003",
                type = PaymentMethodType.PAYPAL,
                displayName = "PayPal",
                isDefault = false
            ),
            PaymentMethod(
                id = "pm_004",
                type = PaymentMethodType.APPLE_PAY,
                displayName = "Apple Pay",
                isDefault = false
            )
        )
        
        // Create Calendar instances for date manipulation
        val now = Calendar.getInstance()
        val twoDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }
        val oneDayAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
        val threeHoursAgo = Calendar.getInstance().apply { add(Calendar.HOUR, -3) }
        val twoHoursAgo = Calendar.getInstance().apply { add(Calendar.HOUR, -2) }
        val oneHourAgo = Calendar.getInstance().apply { add(Calendar.HOUR, -1) }
        val thirtyMinutesAgo = Calendar.getInstance().apply { add(Calendar.MINUTE, -30) }
        val twentyFiveMinutesLater = Calendar.getInstance().apply { add(Calendar.MINUTE, 25) }
        
        // Sample order history
        val sampleOrders = listOf(
            Order(
                id = "ord_001",
                orderNumber = "SP-2024-001",
                items = listOf(
                    OrderItem("item_1", "MacBook Pro 16\"", "Latest model with M3 chip", 1, 2499.99, null),
                    OrderItem("item_2", "Magic Mouse", "Wireless mouse", 1, 79.99, null)
                ),
                status = OrderStatus.DELIVERED,
                orderDate = twoDaysAgo.time,
                deliveryDate = oneDayAgo.time,
                totalAmount = 2579.98,
                deliveryAddress = DeliveryAddress("123 Main St, San Francisco, CA", 37.7749, -122.4194, "Home"),
                pickupAddress = DeliveryAddress("Apple Store, 1 Stockton St, San Francisco, CA", 37.7849, -122.4094),
                driverName = "Mike Johnson",
                driverPhone = "+1 555 123 4567",
                trackingDetails = null,
                paymentMethod = "**** **** **** 1234",
                deliveryFee = 15.00,
                specialInstructions = "Leave at door if not home"
            ),
            Order(
                id = "ord_002",
                orderNumber = "SP-2024-002",
                items = listOf(
                    OrderItem("item_3", "iPhone 15 Pro", "512GB Space Black", 1, 1199.99, null)
                ),
                status = OrderStatus.IN_TRANSIT,
                orderDate = threeHoursAgo.time,
                deliveryDate = null,
                totalAmount = 1199.99,
                deliveryAddress = DeliveryAddress("456 Tech Ave, San Francisco, CA", 37.7849, -122.4094, "Work"),
                pickupAddress = DeliveryAddress("Best Buy, 1717 Harrison St, San Francisco, CA", 37.7649, -122.4194),
                driverName = "Sarah Wilson",
                driverPhone = "+1 555 987 6543",
                trackingDetails = TrackingDetails(
                    currentLocation = DeliveryAddress("Van Ness Ave & Market St, San Francisco, CA", 37.7749, -122.4194),
                    estimatedDeliveryTime = twentyFiveMinutesLater.time,
                    statusHistory = listOf(
                        StatusUpdate(OrderStatus.CONFIRMED, threeHoursAgo.time, "Order confirmed", null),
                        StatusUpdate(OrderStatus.PICKED_UP, twoHoursAgo.time, "Package picked up from store", null),
                        StatusUpdate(OrderStatus.IN_TRANSIT, oneHourAgo.time, "Out for delivery", null)
                    )
                ),
                paymentMethod = "**** **** **** 5678",
                deliveryFee = 12.00,
                specialInstructions = "Call when arrived"
            ),
            Order(
                id = "ord_003",
                orderNumber = "SP-2024-003",
                items = listOf(
                    OrderItem("item_4", "AirPods Pro", "2nd generation with MagSafe", 2, 249.99, null)
                ),
                status = OrderStatus.PENDING,
                orderDate = thirtyMinutesAgo.time,
                deliveryDate = null,
                totalAmount = 499.98,
                deliveryAddress = DeliveryAddress("789 Family Rd, Oakland, CA", 37.8044, -122.2708, "Mom's House"),
                pickupAddress = DeliveryAddress("Target, 1690 Shattuck Ave, Berkeley, CA", 37.8744, -122.2708),
                driverName = null,
                driverPhone = null,
                trackingDetails = null,
                paymentMethod = "**** **** **** 1234",
                deliveryFee = 8.00,
                specialInstructions = null
            ),
            Order(
                id = "ord_003",
                orderNumber = "SP-2024-003",
                items = listOf(
                    OrderItem("item_4", "AirPods Pro", "2nd generation with MagSafe", 2, 249.99, null)
                ),
                status = OrderStatus.PENDING,
                orderDate = thirtyMinutesAgo.time,
                deliveryDate = null,
                totalAmount = 499.98,
                deliveryAddress = DeliveryAddress("789 Family Rd, Oakland, CA", 37.8044, -122.2708, "Mom's House"),
                pickupAddress = DeliveryAddress("Target, 1690 Shattuck Ave, Berkeley, CA", 37.8744, -122.2708),
                driverName = null,
                driverPhone = null,
                trackingDetails = null,
                paymentMethod = "**** **** **** 1234",
                deliveryFee = 8.00,
                specialInstructions = null
            ),
            Order(
                id = "ord_003",
                orderNumber = "SP-2024-003",
                items = listOf(
                    OrderItem("item_4", "AirPods Pro", "2nd generation with MagSafe", 2, 249.99, null)
                ),
                status = OrderStatus.PENDING,
                orderDate = thirtyMinutesAgo.time,
                deliveryDate = null,
                totalAmount = 499.98,
                deliveryAddress = DeliveryAddress("789 Family Rd, Oakland, CA", 37.8044, -122.2708, "Mom's House"),
                pickupAddress = DeliveryAddress("Target, 1690 Shattuck Ave, Berkeley, CA", 37.8744, -122.2708),
                driverName = null,
                driverPhone = null,
                trackingDetails = null,
                paymentMethod = "**** **** **** 1234",
                deliveryFee = 8.00,
                specialInstructions = null
            )
        )

        val sampleProfile = UserProfile(
            name = "John Doe",
            email = "john.doe@example.com",
            phone = "+1 234 567 8900",
            address = "123 Main St, San Francisco, CA",
            memberSince = "January 2024",
            totalDeliveries = 47,
            savedAddresses = sampleAddresses,
            preferredPaymentMethod = "**** **** **** 1234",
            paymentMethods = samplePaymentMethods,
            orderHistory = sampleOrders
        )

        _uiState.value = _uiState.value.copy(
            userProfile = sampleProfile,
            isLoading = false
        )
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(name = name)
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(email = email)
        )
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(phone = phone)
        )
    }

    fun updateAddress(address: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(address = address)
        )
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditing = !_uiState.value.isEditing
        )
    }

    fun saveProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        // Simulate API call
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isEditing = false
            )
        }
    }

    fun showLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = true)
    }

    fun hideLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
        // Handle logout logic here
    }

    fun updateNotificationPreference(type: String, enabled: Boolean) {
        val currentNotifications = _uiState.value.userProfile.deliveryPreferences.notifications
        val updatedNotifications = when (type) {
            "orderUpdates" -> currentNotifications.copy(orderUpdates = enabled)
            "promotions" -> currentNotifications.copy(promotions = enabled)
            "driverLocation" -> currentNotifications.copy(driverLocation = enabled)
            "deliveryConfirmation" -> currentNotifications.copy(deliveryConfirmation = enabled)
            else -> currentNotifications
        }

        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                deliveryPreferences = _uiState.value.userProfile.deliveryPreferences.copy(
                    notifications = updatedNotifications
                )
            )
        )
    }

    fun showPaymentMethodsDialog() {
        _uiState.value = _uiState.value.copy(showPaymentMethodsDialog = true)
    }

    fun hidePaymentMethodsDialog() {
        _uiState.value = _uiState.value.copy(showPaymentMethodsDialog = false)
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        val updatedPaymentMethods = _uiState.value.userProfile.paymentMethods.map { pm ->
            pm.copy(isDefault = pm.id == paymentMethod.id)
        }
        
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                preferredPaymentMethod = paymentMethod.displayName,
                paymentMethods = updatedPaymentMethods
            ),
            showPaymentMethodsDialog = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

