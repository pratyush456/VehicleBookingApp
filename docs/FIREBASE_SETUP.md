# Firebase Setup Guide

## Quick Fix for Build Error

The build is failing because `google-services.json` is missing. You have two options:

### Option 1: Use Mock File (Quick Development)

Copy the template to create a mock file:

```bash
cd /workspaces/VehicleBookingApp
cp app/google-services.json.template app/google-services.json
```

**Note:** This will allow the app to build, but Firebase features (Firestore sync) won't work until you configure a real Firebase project.

### Option 2: Configure Real Firebase Project

1. **Create Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project"
   - Name: "Vehicle Booking App"
   - Follow the setup wizard

2. **Add Android App**
   - In Firebase Console, click "Add app" → Android
   - Package name: `com.vehiclebooking`
   - App nickname: "Vehicle Booking App"
   - Click "Register app"

3. **Download google-services.json**
   - Download the `google-services.json` file
   - Place it in: `/workspaces/VehicleBookingApp/app/google-services.json`

4. **Enable Firestore**
   - In Firebase Console → Firestore Database
   - Click "Create database"
   - Start in production mode
   - Choose a location
   - Create collection: `bookings`

5. **Build the App**
   ```bash
   ./gradlew assembleDebug
   ```

## Firebase Features in the App

### Currently Implemented:
- ✅ Firestore integration for bookings
- ✅ Real-time sync
- ✅ Offline persistence

### Not Yet Implemented:
- Firebase Authentication (using local auth instead)
- Firebase Cloud Messaging
- Firebase Analytics

## Security Rules for Firestore

Once Firestore is enabled, set these security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Bookings collection
    match /bookings/{bookingId} {
      // Allow read if authenticated
      allow read: if request.auth != null;
      
      // Allow write if authenticated and owns the booking
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
                               resource.data.userId == request.auth.uid;
    }
  }
}
```

## Alternative: Disable Firebase (Development Only)

If you don't want to use Firebase right now, you can temporarily disable it:

### 1. Comment out Firebase in build.gradle

```gradle
// In app/build.gradle
// implementation platform('com.google.firebase:firebase-bom:32.7.0')
// implementation 'com.google.firebase:firebase-firestore'
// implementation 'com.google.firebase:firebase-auth-ktx'
```

### 2. Comment out Google Services plugin

```gradle
// In app/build.gradle
// id 'com.google.gms.google-services'
```

### 3. Rebuild

```bash
./gradlew clean assembleDebug
```

**Note:** This will disable Firestore sync, but local Room database will still work.

## Troubleshooting

### Error: "google-services.json is missing"
- Copy the template: `cp app/google-services.json.template app/google-services.json`
- Or download from Firebase Console

### Error: "Default FirebaseApp is not initialized"
- Ensure `google-services.json` is in `app/` directory
- Rebuild the project

### Firestore not syncing
- Check internet connection
- Verify Firestore is enabled in Firebase Console
- Check security rules allow your operations

## Next Steps

1. Copy template or download real `google-services.json`
2. Build the app: `./gradlew assembleDebug`
3. Test offline-first features
4. Configure Firestore security rules (if using real Firebase)
