# Knowlio

Android sample project to display daily knowledge facts.

## Building
1. Install Android SDK (compileSdk 34) and Material Components 1.12.0.
2. Ensure `local.properties` points to your SDK (`sdk.dir=/path/to/sdk`).
3. Run `./gradlew assembleDebug` to build.

The main screen shows a Material3 card with today’s fact fetched from the Room database.
