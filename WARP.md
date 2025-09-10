# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**Supreme Fortnight** is a production-grade Android delivery app built for African markets, featuring ultra-thin gradient bottom sheets and sophisticated event-driven state management. The app uses modern Android development practices with Kotlin, Jetpack Compose, and a clean MVVM architecture enhanced with a custom state machine for complex booking flows.

## Development Commands

### Build & Run
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK on connected device
./gradlew installDebug

# Run the app (requires connected device/emulator)
./gradlew installDebug && adb shell am start -n io.peng.sparrowdelivery/.MainActivity
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run unit tests with coverage
./gradlew testDebugUnitTestCoverage

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.example.BookingStateMachineTest"

# Run tests in specific module
./gradlew :app:test
```

### Code Quality & Linting
```bash
# Run lint checks
./gradlew lint

# Run lint and generate report
./gradlew lintDebug

# Clean build artifacts
./gradlew clean

# Generate dependency report
./gradlew dependencies
```

### Development Setup
```bash
# First-time setup - create local.properties with API keys
cp local.properties.example local.properties
# Edit local.properties to add:
# GOOGLE_MAPS_API_KEY=your_google_maps_key
# HERE_API_KEY=your_here_api_key  
# MAPBOX_ACCESS_TOKEN=your_mapbox_token
```

## Architecture Overview

### Core Architecture Pattern
- **MVVM + State Machine**: Clean MVVM architecture enhanced with a sophisticated state machine for booking flows
- **Event-Driven State Management**: Uses `BookingStateMachine` class to handle complex delivery booking scenarios
- **Clean Architecture**: Separation between domain, data, and presentation layers
- **Repository Pattern**: Abstract data access through repository interfaces

### State Machine Flow
The app's core booking flow follows this state machine:
```
Idle → LocationsEntering → RoutePreview → FindingDriver → DriverFound → BookingConfirmed
  ↓              ↓               ↓             ↓            ↓
Cancel     Find Driver      Cancel       Confirm    Navigate to Tracking
```

Key state machine files:
- `BookingStateMachine.kt` - Core state machine logic and transitions
- `HomeViewModel.kt` - ViewModel that orchestrates state machine events
- State classes: `BookingState`, `BookingEvent`, `BookingSideEffect`

### Directory Structure
```
app/src/main/java/io/peng/sparrowdelivery/
├── core/           # Core utilities, DI, error handling
├── data/           # Data layer (repositories, network, database)
├── domain/         # Domain layer (use cases, entities)
├── presentation/   # UI layer (ViewModels, features)
├── ui/            # Reusable UI components and theme
└── integration/   # External service integrations
```

### Key Components

#### UI Architecture
- **Stitch Design System**: Beautiful design language with Spline Sans typography and distinctive red/green color palette
- **Ultra-Thin Bottom Sheets**: Translucent overlays with frosted glass effects (`SimpleUltraThinBottomSheet.kt`)
- **Stitch Components**: Custom component library in `ui/components/stitch/` (buttons, inputs, cards)
- **Dual Theme Support**: Light theme with cream backgrounds and dark theme variants
- **Material 3 Foundation**: Enhanced with Stitch aesthetics and colors

#### Location & Mapping
- **Google Maps Integration**: Interactive maps with custom overlays
- **Multiple Routing Providers**: HERE API primary, with fallback support
- **Places API**: Address autocomplete and geocoding
- **Real-time Location**: Battery-optimized location tracking

#### Backend Integration
- **Supabase**: Backend-as-a-Service for user management
- **Paystack**: Payment processing optimized for African markets
- **Google Sign-In**: Authentication with Firebase integration

## Development Guidelines

### Working with the State Machine
When modifying booking flows:
1. Always use `handleBookingEvent()` in HomeViewModel to trigger state changes
2. State transitions are defined in `BookingStateMachine.transition()`
3. Side effects (API calls, UI updates) are handled separately via `BookingSideEffect`
4. Never directly modify `BookingState` - always go through events

### Adding New States or Events
1. Add new state to `BookingState` sealed class
2. Add corresponding event to `BookingEvent` sealed class  
3. Add necessary side effects to `BookingSideEffect`
4. Implement state handling in `BookingStateMachine.transition()`

### UI Component Development
- Follow Stitch design patterns from `styles/reference/` HTML designs
- Use `SparrowTheme` and `SparrowColors` for consistent styling (replaces old ShadCN theme)
- Access colors via `LocalSparrowColors.current` or `SparrowTheme.colors` in composables
- Use `SparrowTypography`, `SparrowSpacing`, `SparrowBorderRadius`, and `SparrowElevation` for consistent design
- Maintain translucent overlays for map-based UIs with proper alpha values
- Use Stitch component library: `StitchPrimaryButton`, `StitchTextField`, etc.
- Test components in isolation with `@Preview` annotations wrapped in `SparrowTheme`

### API Integration
- Repository pattern in `data/repositories/`
- Use `ApiResult` wrapper for consistent error handling
- Implement proper loading states and error recovery
- Add fallback mechanisms for critical services (routing, payments)

### Testing Strategy
- Unit tests for state machine logic and ViewModels
- Integration tests for repository implementations
- UI tests for critical user flows (booking, payment)
- Mock external dependencies (APIs, location services)

## Dependencies Management

### Version Catalog
Dependencies are managed via Gradle version catalog in `gradle/libs.versions.toml`:
- **Kotlin**: 2.0.21 with Compose compiler
- **Compose BOM**: 2024.09.00
- **Android Gradle Plugin**: 8.12.1

### Key Dependencies
- **Jetpack Compose**: Declarative UI framework
- **Google Maps Compose**: Map integration
- **Supabase SDK**: Backend services
- **Retrofit**: HTTP client for routing APIs
- **Accompanist**: Additional Compose utilities

### Adding Dependencies
1. Add version to `[versions]` section in `libs.versions.toml`
2. Define library in `[libraries]` section
3. Reference in `app/build.gradle.kts` using `libs.` prefix

## API Keys & Configuration

Required API keys in `local.properties`:
```properties
GOOGLE_MAPS_API_KEY=your_google_maps_key
HERE_API_KEY=your_here_api_key
MAPBOX_ACCESS_TOKEN=your_mapbox_token
```

### Environment Configuration
- **Debug builds**: Use development API endpoints
- **Release builds**: Production endpoints with ProGuard enabled
- **API keys**: Never commit keys to version control
- **BuildConfig**: Auto-generated from local.properties

## Performance Considerations

### Memory Optimization
- Lazy loading of map data and route calculations  
- Proper lifecycle management of location services
- Efficient bitmap handling for driver photos and map markers

### Battery Optimization  
- Location updates only when needed
- Background processing limits
- Efficient coroutine usage for async operations

### State Management
- State machine prevents memory leaks from retained state
- Side effects are cancellable (driver search, route fetching)
- Proper cleanup in ViewModel onCleared()

## External Services Integration

### Google Services
- **Maps SDK**: Interactive mapping with custom styling
- **Places API**: Autocomplete and place details
- **Location Services**: Fused location provider

### HERE Technologies
- **Routing API**: Route calculation and optimization
- **Traffic Data**: Real-time traffic information

### Payment Processing
- **Paystack**: African payment gateway
- **Multiple Methods**: Cards, bank transfers, USSD, mobile money

## Stitch Design System

### Reference Designs
- Beautiful HTML mockups stored in `styles/reference/`
- Consistent color palette: Stitch Red (#EA2A33), Cream (#FCF8F8), Green (#22C55E)
- Spline Sans typography with multiple weights
- Rounded corners (12dp inputs, 16dp cards, 20dp bottom sheets)
- Translucent overlays with proper alpha values

### Component Library
- **Buttons**: `StitchPrimaryButton`, `StitchSecondaryButton`, `StitchSuccessButton`, `StitchIconButton`
- **Inputs**: `StitchTextField`, `StitchSearchField`, `StitchLocationField`
- **Selectors**: `StitchPackageSizeSelector`, `StitchScheduleSelector`
- **Layout**: `UltraThinBottomSheetScaffold`, `TranslucentCard`

### Color Usage
- Primary actions: `stitchColors.primary` (red)
- Secondary actions: `stitchColors.primaryContainer` (cream)
- Success states: `stitchColors.accent` (green)
- Text hierarchy: `onSurface`, `textSecondary`, `textMuted`
- Overlays: `stitchColors.overlay` with proper transparency

### Implementation
- Colors: `ui/theme/SparrowTheme.kt` (SparrowColors, SparrowThemeColors)
- Typography: `ui/theme/SparrowTheme.kt` (SparrowTypography with Spline Sans)
- Spacing & Layout: `ui/theme/SparrowTheme.kt` (SparrowSpacing, SparrowBorderRadius, SparrowElevation)
- Components: `ui/components/stitch/` (Stitch design system components)
- Theme access: `SparrowTheme.colors` or `LocalSparrowColors.current`

## Code Style & Conventions

### Package Naming
- Use reverse domain notation: `io.peng.sparrowdelivery`
- Feature-based organization in presentation layer
- Layer-based organization in core architecture

### Class Naming
- State classes: `BookingState.StateName`
- Events: `BookingEvent.EventName`  
- ViewModels: `FeatureViewModel`
- Repositories: `FeatureRepository` with interface

### Compose Guidelines
- Use `@Stable` and `@Immutable` for performance
- Prefer stateless composables
- Use `remember` for expensive calculations
- Implement proper `@Preview` functions
- Always use `StitchTheme` wrapper in previews
