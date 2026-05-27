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

## Architecture
- **data**: API DTOs, Retrofit service, repository implementation, mappers.
- **domain**: pure models, repository abstraction, use cases.
- **presentation**: activities/fragments/viewmodel/adapter/ui state.

## Why MVVM + Clean Architecture
This separates responsibilities, keeps UI passive, and makes business logic testable and reusable.

## Why Repository Pattern
The repository hides remote and local behavior, letting ViewModel/use cases stay unaware of API persistence limitations.

## Ad insertion behavior
Ads are generated in UI mapping logic by inserting one `AdUi` item after every 5 post items (except trailing insertion at end).

## Update/Delete behavior
Initial data is fetched from API once and cached in-memory inside repository. Updates and deletes mutate in-memory state so UI reflects changes even if backend does not persist.

## Run
1. Open in Android Studio (JDK 17).
2. Sync Gradle.
3. Run app on emulator/device.

## Future improvements
- Add unit tests for use cases and list mapping.
- Add offline cache (Room).
- Add pull-to-refresh + retry UI.
- Add design system tokens and dark-theme tuning.
