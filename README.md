# 🚀 Supreme Fortnight - SparrowDelivery

> **Production-grade delivery app with ultra-thin gradient bottom sheets and event-driven state management**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple)]()
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-blue)]()
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20+%20State%20Machine-orange)]()

## ✨ Features

### 🎨 **Ultra-Premium UI/UX**
- **Ultra-thin gradient bottom sheets** with iOS-style frosted glass effect
- **Multi-weight Inter font family** (400-800) for perfect typography hierarchy
- **ShadCN-inspired component library** for consistent, beautiful UI
- **Smooth animations** and responsive layouts throughout
- **Full-screen map experience** with translucent overlays

### 🏗️ **Production-Grade Architecture**
- **Event-driven state machine** for complex booking flows
- **Clean MVVM architecture** with proper separation of concerns
- **Comprehensive side effects management** for UI synchronization
- **Robust error handling** and recovery mechanisms
- **Version catalog** for dependency management

### 🗺️ **Advanced Location Services**
- **Google Maps integration** with interactive map controls
- **Google Places API** for intelligent address autocomplete
- **HERE API routing** for optimal route calculation
- **Real-time location tracking** with permission management
- **Multiple routing providers** with fallback support

### 💳 **Payment Integration**
- **Paystack integration** optimized for African markets
- **Multiple payment methods**: Cards, bank transfers, USSD, mobile money
- **Nigerian banking support**: First Bank, GTBank, Zenith, etc.
- **Split payments** for driver commissions
- **Refund handling** for cancelled trips

### 🔐 **Authentication & Security**
- **Google Sign-In** with Firebase integration
- **Supabase backend** for user management
- **Secure API key management** with local.properties
- **Data encryption** and secure storage

## 🚗 **Booking Flow State Machine**

Our sophisticated state machine handles complex delivery booking scenarios:

```
Idle → LocationsEntering → RoutePreview → FindingDriver → DriverFound → BookingConfirmed
  ↓                            ↓               ↓             ↓            ↓
Cancel                    Find Driver      Cancel       Confirm       Navigate to
                                                                      Tracking
```

## 🛠️ **Tech Stack**

### **Frontend**
- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Google's design system
- **Navigation Compose** - Type-safe navigation

### **Backend & Services**
- **Supabase** - Backend-as-a-Service
- **Google Maps SDK** - Interactive mapping
- **Google Places API** - Location services  
- **HERE API** - Route optimization
- **Paystack** - Payment processing

## 🚀 **Getting Started**

### **Prerequisites**
- Android Studio Hedgehog | 2024.2.1+
- Android SDK 26+
- Kotlin 1.9+

### **Setup**

1. **Clone the repository**
   ```bash
   git clone https://github.com/alflotsu/supreme-fortnight.git
   cd supreme-fortnight
   ```

2. **Add API Keys**
   Create `local.properties` in root directory:
   ```properties
   GOOGLE_MAPS_API_KEY=your_google_maps_key
   HERE_API_KEY=your_here_api_key
   MAPBOX_ACCESS_TOKEN=your_mapbox_token
   ```

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

## 🏆 **Key Innovations**

### **Ultra-Thin Bottom Sheets**
- Beautiful gradient translucency that doesn't block map view
- iOS-inspired frosted glass effects
- Proper state-driven visibility controls

### **Event-Driven Architecture**
- Clean separation of business logic and UI
- Predictable state transitions
- Comprehensive side effects management

### **Production-Ready Features**
- Full-screen map rendering
- Smart error recovery
- Memory-efficient location services
- Battery optimization

## 👨‍💻 **Author**

**Alfred Lotsu** - *Founder & Lead Developer*
- GitHub: [@alflotsu](https://github.com/alflotsu)
- Project: [Supreme Fortnight](https://github.com/alflotsu/supreme-fortnight)

---

### 🌟 **Built with passion for African delivery markets** 

*Leveraging cutting-edge Android development practices to create world-class delivery experiences*

---

**⭐ If this project helps you, please give it a star!**
