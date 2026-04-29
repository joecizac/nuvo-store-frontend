# PRD: Nuvo Store (Online Delivery Platform)

## 1. Goal

Agnostic online delivery platform using Compose Multiplatform (Android/iOS). MVI + Clean
Architecture.

## 2. Tech Stack

- **Framework:** Compose Multiplatform (CMP)
- **UI:** Material 3 Expressive (Light/Dark)
- **Navigation:** Navigation3 (androidx.navigation.compose)
- **State Management:** ViewModel KMP + StateFlow (MVI pattern)
- **DI:** Koin
- **Network:** Ktor Client
- **Local DB:** Room KMP
- **Image Loading:** Coil 3
- **Authentication:** Firebase Auth (JWT verification on backend)
- **Notifications:** Firebase Cloud Messaging (FCM)

## 3. Implementation Plan (Phase-wise)

### Phase 1: Foundation & Infrastructure

- [x] Initialize CMP Project (Multi-module structure).
- [x] Setup `core:designsystem` (M3 Expressive, Themes, Typography).
- [x] Setup `core:network` (Ktor config, Koin modules, Base Response).
- [x] Implement `core:mvi` base classes for State/Intent.
- [x] Base Navigation3 graph setup in `composeApp`.

### Phase 2: Auth & User Profile

- [x] `feature:auth`: Firebase Auth integration (Email/Social) [Mock implemented].
- [x] JWT Interceptor for Ktor (Secure API calls).
- [x] `api/v1/users/me` integration for profile data.
- [x] `api/v1/users/me/addresses` (List user addresses).

### Phase 3: Store Discovery & Catalog

- [x] `feature:discovery`: Nearby stores (Map/List) via `api/v1/stores`.
- [x] Geolocation integration (Lat/Lng query).
- [x] `feature:catalog`: Store detail screen.
- [x] Category & Product listing via `api/v1/stores/{id}/products`.
- [x] Unit tests for Discovery & Catalog components.

### Phase 4: Cart & Order Management

- [x] `data:local`: Room KMP for local cart persistence.
- [x] `feature:cart`: Add/Update/Remove items via `api/v1/cart`.
- [x] Logic for single-store cart restriction.
- [x] `feature:checkout`: Address selection + Place order (`api/v1/orders`).

### Phase 5: Polishing & Real-time Features

- [ ] FCM Integration for Order Status updates.
- [ ] Reviews & Ratings implementation.
- [ ] Favourites (Store/Product) toggle.
- [ ] Final UI/UX polish (Material 3 Expressive details).
- [ ] Multi-industry branding support (Configurable themes/labels).

## 4. Module Map (Feature-Driven Multimodule)

- `:composeApp` (App entry, navigation root)
- `:core:designsystem` (M3 Expressive, shared UI)
- `:core:network` (Ktor, JWT interceptor, API models)
- `:core:mvi` (Base State/Intent/ViewModel)
- `:domain` (Entities, Repository interfaces, UseCases)
- `:data` (Repository impls, Room DB, Ktor clients)
- `:feature:auth` (Login, Registration, Firebase Auth)
- `:feature:discovery` (Store listing, Geolocation)
- `:feature:catalog` (Store details, Product listing)
- `:feature:cart` (Shopping cart management)
- `:feature:checkout` (Order placement, Address selection)
