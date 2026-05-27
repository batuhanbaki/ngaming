# Ngaming Android Case Study

## Project summary
A production-style Kotlin Android app that fetches posts from JSONPlaceholder, renders them in a RecyclerView with mixed post/ad view types, supports swipe delete for posts only, and updates posts from a bottom sheet editor.

## Tech stack
- Kotlin
- Clean Architecture + MVVM
- Hilt DI
- Retrofit + OkHttp logging interceptor
- Coroutines + Flow
- RecyclerView ListAdapter + DiffUtil
- Coil image loading
- Material Components + ViewBinding
- Facebook Shimmer + SwipeRefreshLayout

## Architecture
- **data**: API DTOs, Retrofit service, repository implementation, mappers.
- **domain**: pure models, repository abstraction, use cases.
- **presentation**: activities/fragments/viewmodel/adapter/ui state.

## UI/UX improvements
- **Shimmer loading**: Loading now uses skeleton rows that mimic the post layout (circular avatar + title/body lines). Shimmer is shown only in loading, and stopped/hidden in success, empty, or error to avoid flicker.
- **Modern feed**: Material toolbar, soft background, card spacing, subtle typography hierarchy, rounded post/ad cards, and pull-to-refresh.
- **Post rows**: Circular grayscale images, metadata (`Post #id`), strong title, 2-line description, ripple feedback.
- **Ad rows**: Native-looking sponsored cards with ad label, title/description/CTA, and distinct but subtle styling.

## Ad insertion strategy
Ads are not generated in adapter bind logic. Instead, the ViewModel maps domain posts into a sealed `PostListItem` feed:
- `PostUi(post, imageSeed)`
- `AdUi(stableId, title, description, ctaText)`

Ad placement alternates intervals of **4 or 5 posts** via a **seeded Random** derived from current post IDs.
This provides realistic variety while staying stable for the current list snapshot, so ads do not jump on small UI updates.

## Swipe/delete behavior
- Swipe gesture is enabled only for `PostUi` items.
- `AdUi` items are not deletable and are blocked from swipe directions.
- Deleting a post updates domain data and rebuilds the UI feed with logical ad insertion.

## Update flow
- Clicking a post opens the detail/update bottom sheet.
- Saving title/body updates repository state.
- ViewModel observes posts, rebuilds `PostListItem`, and submits through DiffUtil.

## Why this structure
Keeping ad placement in ViewModel-side mapping and keeping adapter purely render-focused preserves separation of concerns and keeps Clean Architecture + MVVM boundaries intact.

## Run
1. Open in Android Studio (JDK 17).
2. Sync Gradle.
3. Run app on emulator/device.

## Centralized Logging
- Added `AppLogger` abstraction with `AppLoggerImpl` so logging is centralized and not scattered via direct `android.util.Log` calls.
- Logging is debug-only (`BuildConfig.DEBUG`) to minimize/noise in release builds.
- Sensitive data is intentionally not logged (no tokens/passwords/certificates/request bodies/private user data).

### Logged areas
- App lifecycle: startup and network monitor initialization.
- Network monitor: callback registration and connectivity transitions (`available`, `unavailable`, `losing`, `lost`, unregister errors).
- Network interceptor: request allowed/blocked by connectivity state (host/path only).
- Retrofit/OkHttp: `HttpLoggingInterceptor` at BASIC in debug, NONE in release.
- Repository: fetch start/success/failure and local update/delete mutations.
- Use cases and ViewModel: load/retry state transitions, success/empty/error emissions, update/delete actions.
- Feed builder: source count, stable seed, ad insertion intervals (4/5), final feed/ad totals.
- Detail screen: open/save/validation/update/dismiss events.
- Error mapping: NoInternet, SSL/certificate, HTTP, IO, and unknown error types.

This improves debuggability for startup flow, connectivity handling, API behavior, SSL pinning failures, ad feed generation, and post update/delete operations while preserving architecture boundaries.
