# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is an Android application for vehicle booking that allows users to book vehicles by selecting source, destination, and travel date. The app sends notifications to vehicle owners when new booking requests are made and stores bookings locally.

**Key Technologies:**
- Android SDK (API 21+, target SDK 34)
- Java 8
- AndroidX libraries
- Material Design components
- Gson for JSON serialization
- SharedPreferences for local data storage

## Common Development Commands

### Building and Running
```bash
# Clean and build the project
./gradlew clean build

# Install debug APK on connected device/emulator
./gradlew installDebug

# Build release APK
./gradlew assembleRelease

# Run all tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint

# Clean project (removes build directories)
./gradlew clean
```

### Testing and Debugging
```bash
# Run specific test class
./gradlew test --tests="*.BookingRequestTest"

# Generate test coverage report
./gradlew jacocoTestReport

# Check dependencies
./gradlew dependencies

# View project structure
./gradlew projects
```

## Architecture Overview

### Core Components

**MainActivity** (`com.vehiclebooking.MainActivity`)
- Entry point with two main actions: "Book a Vehicle" and "View My Bookings"
- Uses simple button navigation to launch BookingActivity

**BookingActivity** (`com.vehiclebooking.BookingActivity`)
- Contains the main booking form with source, destination, and date picker
- Validates input and creates BookingRequest objects
- Integrates with NotificationHelper to send notifications
- Includes date validation (no past dates allowed)

**BookingRequest** (`com.vehiclebooking.BookingRequest`)
- Data model representing a booking request
- Contains source, destination, travel date, and timestamp
- Provides formatted output methods for display and notifications

### Data and Notification Layer

**BookingStorage** (`com.vehiclebooking.BookingStorage`)
- Handles local persistence using SharedPreferences and Gson
- Stores/retrieves booking requests as JSON
- Provides methods for saving, retrieving, and clearing bookings

**NotificationHelper** (`com.vehiclebooking.NotificationHelper`)
- Manages push notifications to alert vehicle owners
- Creates notification channels (Android O+)
- Automatically saves bookings when sending notifications
- Handles notification permissions gracefully

**NotificationService** (`com.vehiclebooking.NotificationService`)
- Background service for potential future server-side notifications
- Currently minimal implementation, ready for extension

### Key Design Patterns

**Data Flow:** User input → BookingRequest creation → Notification sending → Local storage
**Notification Chain:** BookingActivity → NotificationHelper → BookingStorage
**Date Handling:** Calendar objects with SimpleDateFormat for consistent date formatting

## Important Configuration Details

### Dependencies
The app uses specific versions of key libraries:
- Material Design: `1.9.0`
- AndroidX AppCompat: `1.6.1`
- ConstraintLayout: `2.1.4`
- Gson: `2.10.1`

### Permissions Required
- `INTERNET` - For future online features
- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION` - For location services
- `POST_NOTIFICATIONS` - For sending booking notifications

### Build Configuration
- Namespace: `com.vehiclebooking`
- Min SDK: 21 (Android 5.0)
- Target SDK: 34 (Android 14)
- Java compatibility: VERSION_1_8

## Development Guidelines

### Adding New Features
When extending the app:
- Follow the existing pattern of separating UI (Activities) from business logic (Helper classes)
- Use the BookingStorage class for any new data persistence needs
- Extend NotificationHelper for new notification types
- Add proper input validation in Activities before processing data

### Testing Considerations
- Test date picker validation (no past dates)
- Verify notification permissions are handled
- Test data persistence across app restarts
- Validate form input handling and error messages

### Resource Management
- String resources are centralized in `app/src/main/res/values/strings.xml`
- Colors are defined in `app/src/main/res/values/colors.xml`
- Layouts follow Material Design principles with ConstraintLayout

### Common Issues to Watch For
- Notification permissions on Android 13+ devices
- Date picker minimum date validation
- SharedPreferences JSON serialization/deserialization
- Activity lifecycle handling during booking submission

## Future Enhancement Areas

The README.md indicates several planned improvements:
- Online backend integration
- Payment processing capabilities
- GPS auto-location detection
- Admin panel for booking management
- Server-side push notifications
- Complete booking history management

When implementing these features, maintain the existing architecture patterns and ensure backward compatibility with local storage.