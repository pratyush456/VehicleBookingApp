# Vehicle Booking App

A simple Android application that allows users to book vehicles by selecting source location, destination, and travel date. Vehicle owners receive notifications when new booking requests are made.

## Features

- **User-friendly Booking Interface**: Simple form to input source, destination, and travel date
- **Date Selection**: Interactive date picker with validation (no past dates)
- **Instant Notifications**: Real-time notifications to vehicle owner when bookings are made
- **Local Data Storage**: Bookings are saved locally for future reference
- **Clean UI**: Material Design-based interface with intuitive navigation

## How It Works

1. **Main Screen**: Users see options to "Book a Vehicle" or "View My Bookings"
2. **Booking Form**: Users fill in:
   - Source location (From where)
   - Destination location (To where)
   - Travel date (using date picker)
3. **Booking Submission**: When submitted, the app:
   - Validates all fields are filled
   - Sends a notification to the vehicle owner
   - Saves the booking locally
   - Shows confirmation to the user

## Project Structure

```
VehicleBookingApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/vehiclebooking/
│   │   │   ├── MainActivity.java          # Main landing screen
│   │   │   ├── BookingActivity.java       # Booking form screen
│   │   │   ├── BookingRequest.java        # Data model for bookings
│   │   │   ├── NotificationHelper.java    # Handles notifications
│   │   │   ├── BookingStorage.java        # Local data storage
│   │   │   └── NotificationService.java   # Background service
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml      # Main screen layout
│   │   │   │   └── activity_booking.xml   # Booking form layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml            # App text resources
│   │   │   │   ├── colors.xml             # Color definitions
│   │   │   │   └── styles.xml             # App themes
│   │   │   └── AndroidManifest.xml        # App configuration
│   │   └── build.gradle                   # App-level build config
│   └── proguard-rules.pro                 # ProGuard configuration
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties      # Gradle wrapper config
├── build.gradle                           # Project-level build config
├── settings.gradle                        # Project settings
├── gradle.properties                      # Gradle properties
└── README.md                             # This file
```

## Setup Instructions

### Prerequisites
- Android Studio (recommended version: Arctic Fox or later)
- Android SDK (API level 21 or higher)
- Java 8 or higher

### Installation Steps

1. **Clone/Download the project**
   ```bash
   git clone <repository-url>
   cd VehicleBookingApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the VehicleBookingApp folder and select it

3. **Sync Project**
   - Android Studio will automatically sync the project
   - If not, click "Sync Now" when prompted

4. **Build the Project**
   - Go to Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or use the build button in the toolbar

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click the Run button (green play icon)

### Manual Build (Command Line)

If you prefer using command line:

```bash
# Clean and build the project
./gradlew clean build

# Install debug APK on connected device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

## Key Components Explained

### MainActivity
The main entry point of the app with two primary buttons:
- **Book a Vehicle**: Opens the booking form
- **View My Bookings**: (Future feature) Shows booking history

### BookingActivity
The booking form where users:
- Enter source and destination
- Select travel date using DatePicker
- Submit the booking request

### NotificationHelper
Manages push notifications to alert you (the vehicle owner) of new bookings. Includes:
- Notification channel setup
- Rich notification content with booking details
- Automatic notification ID generation

### BookingStorage
Handles local data persistence using SharedPreferences and JSON:
- Saves booking requests locally
- Retrieves all saved bookings
- Uses Gson for JSON serialization

## Permissions Required

The app requests these permissions:
- `INTERNET`: For potential future online features
- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: For location-based features
- `POST_NOTIFICATIONS`: To send booking notifications

## Customization Options

### Adding Your Vehicle Information
You can modify the app to include:
- Vehicle types and availability
- Pricing information
- Contact details

### Extending Notifications
The notification system can be enhanced to:
- Send SMS or email notifications
- Integrate with external booking systems
- Add booking confirmation/rejection features

### UI Customization
- Colors can be modified in `app/src/main/res/values/colors.xml`
- Text can be updated in `app/src/main/res/values/strings.xml`
- Layouts can be customized in the respective XML files

## Future Enhancements

1. **Online Backend**: Connect to a server for real-time booking management
2. **Payment Integration**: Add payment processing capabilities
3. **GPS Integration**: Auto-detect user location
4. **Admin Panel**: Web interface for managing bookings
5. **Push Notifications**: Server-side push notifications
6. **Booking History**: Complete booking management system

## Troubleshooting

### Common Issues

1. **Build Errors**
   - Ensure you have the latest Android SDK
   - Clean and rebuild the project
   - Check internet connection for dependency downloads

2. **Notification Not Working**
   - Check if notification permissions are granted
   - Ensure the app is not in battery optimization mode

3. **Date Picker Issues**
   - Verify the device date/time settings are correct

## Support

This is a basic implementation designed to get you started. You can extend and customize it based on your specific business needs.

## License

This project is created for personal/business use. Feel free to modify and adapt it according to your requirements.