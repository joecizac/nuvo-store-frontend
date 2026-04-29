# PRD: Nuvo Store (Online Delivery Platform)

## 1. Goal

Agnostic delivery platform. Compose Multiplatform (Android/iOS). MVI + Clean Architecture.

## 2. Tech Stack

- **Framework:** CMP
- **UI:** Material 3 Expressive
- **Navigation:** Navigation3
- **State:** ViewModel KMP + StateFlow (MVI)
- **DI:** Koin
- **Network:** Ktor
- **Local DB:** Room KMP
- **Image:** Coil 3
- **Auth:** Firebase Auth (JWT backend verify)
- **Notify:** FCM

## 3. Implementation Plan

### Phase 1: Foundation & Infrastructure

- [x] CMP Project Init.
- [x] `:core:designsystem` (M3 Expressive).
- [x] `:core:network` (Ktor, Koin, Base Response).
- [x] `:core:mvi` base.
- [x] Base Navigation3 graph.

### Phase 2: Auth & User Profile

- [x] `:feature:auth` (Firebase mock).
- [x] JWT Interceptor.
- [x] `api/v1/users/me` profile.
- [x] `api/v1/users/me/addresses`.

### Phase 3: Store Discovery & Catalog

- [x] `:feature:discovery` (Stores via `api/v1/stores`).
- [x] Geolocation.
- [x] `:feature:catalog` (Store detail).
- [x] Product list via `api/v1/stores/{id}/products`.
- [x] Tests for Discovery & Catalog.

### Phase 4: Cart & Order Management

- [x] `:data:local` (Room KMP).
- [x] `:feature:cart` (Add/Update/Remove via `api/v1/cart`).
- [x] Single-store restriction logic.
- [x] `:feature:checkout` (Address + Order via `api/v1/orders`).

### Phase 5: Polishing & Real-time Features

- [ ] FCM Order Status updates.
- [ ] Reviews & Ratings.
- [ ] Favourites toggle.
- [ ] Migrate to `jetbrains.compose.resources`.
- [ ] Final UI/UX polish (M3 details).
- [ ] Multi-industry branding.

## 4. Module Map

- `:composeApp` (Entry, nav)
- `:core:designsystem` (M3, shared UI)
- `:core:network` (Ktor, JWT, models)
- `:core:mvi` (Base MVI)
- `:domain` (Entities, Repos, UseCases)
- `:data` (Impls, Room, Ktor)
- `:feature:auth`
- `:feature:discovery`
- `:feature:catalog`
- `:feature:cart`
- `:feature:checkout`
