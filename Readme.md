# GeoMinder App 📍
GeoMinder is a location-based reminder application designed to help users manage tasks effectively by creating geofenced reminders. This app leverages modern Android development tools and libraries, including Jetpack Compose, Hilt for dependency injection, and geofencing with Google's Geofencing APIs.

## 📝 Note
I am unable to display the notification UI due to issues related to the Google Maps API billing configuration. However, the provided code is fully functional and operates as expected under correct notification setup.

## Features 🚀
- Add Reminders: Create reminders with a title, location, and geofence radius.
- View Reminders: View a list of all saved reminders.
- Delete Reminders: Remove unwanted reminders with a simple click.
- Geofencing: Notifications trigger when entering or exiting a defined geofence area.
- Location Permissions: Request and manage location permissions dynamically.

## Tech Stack 💻
- Programming Language: Kotlin
- UI Framework: Jetpack Compose
- Dependency Injection: Hilt
- Navigation: Jetpack Navigation Compose
- Geofencing API: Google Geofencing API
- MVVM: The App follows the MVVM Architecture
- 

## 📸 Screenshots

### 🌙 Dark Mode
<p align="center"> <img src="./Screenshot 0 Dark.jpg" alt="Dark Mode Screenshot 1" width="180" /> <img src="./Screenshot 1 Dark.jpg" alt="Dark Mode Screenshot 2" width="180" /> </p>

### ☀️ Light Mode
<p align="center"> <img src="./Screenshot 0 Light.jpg" alt="Light Mode Screenshot 1" width="180" /> <img src="./Screenshot 1 Light.jpg" alt="Light Mode Screenshot 2" width="180" /> </p>

## Permissions 🛑
This app requires the following permissions for geofencing:
- android.permission.INTERNET
- android.permission.ACCESS_FINE_LOCATION
- android.permission.ACCESS_COARSE_LOCATION
- android.permission.ACCESS_BACKGROUND_LOCATION"(Required for geofence to work in the background).
Ensure to grant these permissions during runtime and for more details do check Manifest file.

## How to Run 🏃‍♂️
1. Clone this repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle.
4. Build and run the app on a physical device or emulator with Google Play Services.

## 🤝 Contributing
Feel free to fork this repository and submit pull requests to improve the project! 🎉

## License 📜
This project is licensed under the MIT License.

## 💬 Feedback
If you have any feedback or issues, please open an issue or reach out. 😊