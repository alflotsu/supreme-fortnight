package io.peng.sparrowdelivery.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
// TODO: Replace with Lucide and Phosphor when libraries are available
// Using Material Icons as placeholders for now

/**
 * SparrowIcons - Unified icon system inspired by SF Symbols design principles
 * 
 * Strategy:
 * - Lucide: UI framework elements (navigation, actions, system)
 * - Phosphor: Delivery features and user-facing elements
 * 
 * SF Symbols Inspiration:
 * - Consistent sizing (20pt base = 24dp)
 * - Visual hierarchy through weight variation
 * - Contextual variants for different states
 */
object SparrowIcons {
    
    // SF Symbols-inspired sizing system
    object Size {
        val Small = 16.dp      // SF Small (16pt)
        val Regular = 20.dp    // SF Regular (20pt) - Primary size
        val Medium = 24.dp     // SF Medium (24pt) - Default for most UI
        val Large = 28.dp      // SF Large (28pt) - Prominent actions
        val XLarge = 32.dp     // SF XLarge (32pt) - Hero elements
    }
    
    // ============================================
    // UI FRAMEWORK ICONS (Lucide)
    // Clean, geometric, precise - perfect for chrome
    // ============================================
    
    object UI {
        // Navigation - Lucide-style (geometric, precise)
        val ArrowBack: ImageVector get() = Icons.Default.ArrowBack
        val ArrowForward: ImageVector get() = Icons.Default.ArrowForward  
        val ArrowUp: ImageVector get() = Icons.Default.KeyboardArrowUp
        val ArrowDown: ImageVector get() = Icons.Default.KeyboardArrowDown
        val ChevronLeft: ImageVector get() = Icons.Default.KeyboardArrowLeft
        val ChevronRight: ImageVector get() = Icons.Default.KeyboardArrowRight
        val ChevronUp: ImageVector get() = Icons.Default.KeyboardArrowUp
        val ChevronDown: ImageVector get() = Icons.Default.KeyboardArrowDown
        
        // Actions
        val Close: ImageVector get() = Icons.Default.Close
        val Menu: ImageVector get() = Icons.Default.Menu
        val More: ImageVector get() = Icons.Default.MoreVert
        val Settings: ImageVector get() = Icons.Default.Settings
        val Search: ImageVector get() = Icons.Default.Search
        val Filter: ImageVector get() = Icons.Default.FilterList
        val Sort: ImageVector get() = Icons.Default.Sort
        
        // Content Actions
        val Add: ImageVector get() = Icons.Default.Add
        val Remove: ImageVector get() = Icons.Default.Remove
        val Edit: ImageVector get() = Icons.Default.Edit
        val Delete: ImageVector get() = Icons.Default.Delete
        val Copy: ImageVector get() = Icons.Default.ContentCopy
        val Share: ImageVector get() = Icons.Default.Share
        val Save: ImageVector get() = Icons.Default.Save
        
        // States
        val Check: ImageVector get() = Icons.Default.Check
        val CheckCircle: ImageVector get() = Icons.Default.CheckCircle
        val Alert: ImageVector get() = Icons.Default.Warning
        val Info: ImageVector get() = Icons.Default.Info
        val Warning: ImageVector get() = Icons.Default.Warning
        val Error: ImageVector get() = Icons.Default.ErrorOutline
        
        // Visibility
        val Visible: ImageVector get() = Icons.Default.Visibility
        val Hidden: ImageVector get() = Icons.Default.VisibilityOff
        val Lock: ImageVector get() = Icons.Default.Lock
        val Unlock: ImageVector get() = Icons.Default.LockOpen
    }
    
    // ============================================
    // DELIVERY FEATURES (Phosphor)
    // Warm, friendly, approachable - perfect for user features
    // ============================================
    
    object Delivery {
        // Vehicles & Transport - Phosphor-style (warm, friendly)
        val Truck: ImageVector get() = Icons.Default.LocalShipping
        val Car: ImageVector get() = Icons.Default.DirectionsCar
        val Motorcycle: ImageVector get() = Icons.Default.TwoWheeler
        val Bicycle: ImageVector get() = Icons.Default.DirectionsBike
        val Van: ImageVector get() = Icons.Default.LocalShipping
        
        // Packages & Items
        val Package: ImageVector get() = Icons.Default.LocalShipping
        val PackageCheck: ImageVector get() = Icons.Default.CheckCircle
        val Box: ImageVector get() = Icons.Default.Inventory2
        val Gift: ImageVector get() = Icons.Default.CardGiftcard
        val ShoppingBag: ImageVector get() = Icons.Default.ShoppingBag
        
        // Location & Navigation
        val MapPin: ImageVector get() = Icons.Default.LocationOn
        val Navigation: ImageVector get() = Icons.Default.Navigation
        val Route: ImageVector get() = Icons.Default.Route
        val Compass: ImageVector get() = Icons.Default.Explore
        val GPS: ImageVector get() = Icons.Default.GpsFixed
        
        // Time & Scheduling  
        val Clock: ImageVector get() = Icons.Default.Schedule
        val Timer: ImageVector get() = Icons.Default.Timer
        val Calendar: ImageVector get() = Icons.Default.CalendarMonth
        val CalendarCheck: ImageVector get() = Icons.Default.EventAvailable
        val Schedule: ImageVector get() = Icons.Default.CalendarToday
        
        // Status & Tracking
        val Pending: ImageVector get() = Icons.Default.Schedule
        val InTransit: ImageVector get() = Icons.Default.LocalShipping
        val Delivered: ImageVector get() = Icons.Default.CheckCircle
        val Cancelled: ImageVector get() = Icons.Default.Cancel
        val Delayed: ImageVector get() = Icons.Default.Warning
        
        // Communication
        val Phone: ImageVector get() = Icons.Default.Phone
        val Message: ImageVector get() = Icons.Default.Message
        val Call: ImageVector get() = Icons.Default.Call
        val SMS: ImageVector get() = Icons.Default.Sms
        
        // Payment & Money
        val Payment: ImageVector get() = Icons.Default.CreditCard
        val Money: ImageVector get() = Icons.Default.AttachMoney
        val Receipt: ImageVector get() = Icons.Default.Receipt
        val Wallet: ImageVector get() = Icons.Default.AccountBalanceWallet
    }
    
    // ============================================
    // USER & PROFILE (Phosphor)
    // Human-centered, approachable
    // ============================================
    
    object User {
        val Profile: ImageVector get() = Icons.Default.Person
        val Users: ImageVector get() = Icons.Default.Group
        val Driver: ImageVector get() = Icons.Default.AccountCircle
        val Customer: ImageVector get() = Icons.Default.Person
        val Admin: ImageVector get() = Icons.Default.AdminPanelSettings
        
        // Authentication
        val Login: ImageVector get() = Icons.Default.Login
        val Logout: ImageVector get() = Icons.Default.Logout
        val Register: ImageVector get() = Icons.Default.PersonAdd
        
        // Notifications
        val Notification: ImageVector get() = Icons.Default.Notifications
        val NotificationOff: ImageVector get() = Icons.Default.NotificationsOff
        val Alert: ImageVector get() = Icons.Default.NotificationImportant
    }
    
    // ============================================
    // SYSTEM & UTILITY (Mixed strategy)
    // Use most appropriate icon for context
    // ============================================
    
    object System {
        // Network & Connectivity
        val WiFi: ImageVector get() = Icons.Default.Wifi
        val WiFiOff: ImageVector get() = Icons.Default.WifiOff
        val Signal: ImageVector get() = Icons.Default.SignalCellularAlt
        val Offline: ImageVector get() = Icons.Default.CloudOff
        
        // File & Data
        val Download: ImageVector get() = Icons.Default.Download
        val Upload: ImageVector get() = Icons.Default.Upload
        val File: ImageVector get() = Icons.Default.InsertDriveFile
        val Folder: ImageVector get() = Icons.Default.Folder
        val Image: ImageVector get() = Icons.Default.Image
        
        // Feedback
        val Like: ImageVector get() = Icons.Default.ThumbUp
        val Dislike: ImageVector get() = Icons.Default.ThumbDown
        val Star: ImageVector get() = Icons.Default.StarBorder
        val StarFilled: ImageVector get() = Icons.Default.Star
        val Heart: ImageVector get() = Icons.Default.FavoriteBorder
        val HeartFilled: ImageVector get() = Icons.Default.Favorite
    }
}

/**
 * SF Symbols-inspired icon variants
 * Provides different visual weights for hierarchy
 */
enum class SparrowIconWeight {
    LIGHT,    // Subtle, secondary elements
    REGULAR,  // Default weight
    BOLD,     // Emphasized, important actions  
    FILL      // Solid, active states
}

/**
 * Context-aware icon selection
 * Mimics SF Symbols' contextual variants
 */
enum class SparrowIconContext {
    NAVIGATION,   // Back buttons, chevrons
    ACTION,       // Buttons, interactive elements  
    STATUS,       // States, indicators
    CONTENT,      // Within text, inline
    DECORATIVE    // Pure visual, non-functional
}
