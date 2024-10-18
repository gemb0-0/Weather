Weather App

A comprehensive weather application built using Android, Kotlin, and modern Android development practices. This app provides real-time weather updates, including current conditions, hourly forecasts, and weekly forecasts, along with various user preferences such as language, temperature units, wind speed, and notifications.

Features

Current Weather: Displays current weather conditions including temperature, humidity, wind speed, and weather description.

Hourly Forecast: Provides weather updates for each hour of the day.

Weekly Forecast: Shows weather predictions for the entire week.

Favorites Management: Users can mark locations as favorites, allowing quick access to weather information for multiple places.

Map Integration: Integrated with Google Places API and MapsFragment for selecting locations.

Settings: Customize settings for:

Language ( English, Arabic)

Temperature unit (Celsius or Fahrenheit or Kelvin)

Wind speed (km/h or mph)

Location services (use GPS or manual location input)

Tech Stack

Kotlin: Main language for development

MVVM Architecture: For separation of concerns and scalable codebase

Room: Local database for storing favorite locations

Retrofit: For fetching weather data from OpenWeatherMap API

Coroutines: For handling background threads and asynchronous tasks

Google Places API: For location searching and autocomplete

SharedPreferences: For saving user preferences and settings

WorkManager / BroadcastReceiver: For network state listening and handling network connectivity changes

