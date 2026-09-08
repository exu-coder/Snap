# EXU Media Downloader

A local-first Android media downloader MVP based on the supplied PLAN.md.

## Stack
- Kotlin
- Jetpack Compose / Material 3
- Clean Architecture direction + MVVM foundation
- Room / DataStore dependencies
- OkHttp + Coroutines
- WorkManager dependency
- AndroidX Media3 dependency
- Share Sheet `ACTION_SEND`
- HTTP/HTTPS `ACTION_VIEW`

## Current MVP
- Premium bold Compose UI
- Floating dock navigation
- Share Sheet URL reception
- URL input
- Download queue UI model
- Direct-link validation gate
- Foundation dependencies for Room, WorkManager, Media3, OkHttp and DataStore

## Next implementation
1. Implement `DownloadEngine` with streaming, HTTP Range resume, pause/resume/cancel/retry.
2. Persist queue/history with Room.
3. Add foreground download execution + progress notification.
4. Add WebView browser and permitted direct-resource detection.
5. Add MediaStore library and Media3 player.
6. Add provider adapters only where access is authorized and technically permitted.

The project intentionally does not bypass DRM, authentication barriers, private content controls, paywalls, or platform protections.
