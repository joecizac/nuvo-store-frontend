# Technical Documentation: Nuvo Store

## 1. Architecture and Implementation

### Foundation: Clean Architecture & MVI

Concerns separated into layers. MVI presentation for unidirectional flow.

* **Model (State):** Single source of truth.
* **View (Composable):** UI observing state, emit intents.
* **Intent (Action):** Trigger state change.
* **Effect (Side-Effect):** Transient events (Snackbar/Nav).

### Module Structure

Feature-driven multi-module:

* **`:composeApp`:** Nav root + Koin init.
* **`:core:mvi`:** Base classes + shared utils.
* **`:core:network`:** Ktor + JWT interceptor.
* **`:core:designsystem`:** M3 Expressive theme + shared UI.
* **`:domain`:** Pure layer. Entities + Repo interfaces.
* **`:data`:** Impls. Room KMP + Ktor.
* **`:feature:*`:** Encapsulated MVI features.

---

## 2. Technical Decisions

### Room KMP for Cart

Offline-first experience. Familiar API, Flow support, type-safety across targets.

### Cents-Based Currency (`Long`)

Internal price representation. Avoid `Double` precision drift. 100% accuracy.

### Debounced Remote Sync

2s debounce on cart sync. Prevent API spam on rapid clicks. Batch network requests.

### Standardized MVI Side-Effects

Separate persistent `State` from transient `Effect`. Fix re-trigger on config change.

---

## 3. Technical Scope for Improvement

### Resource Migration

Move hardcoded strings to `jetbrains.compose.resources`. Localization + assets.

### Logging

Replace `println` with multiplatform logger (Kermit/Napier).

### Pagination

Add Paging 3 KMP for large catalogs.

### Testing Depth

Add UI Tests (Compose) + Integration Tests for cross-feature flows.

---

## 4. Required API Endpoints

### Address Management

`POST/PUT/DELETE /api/v1/users/me/addresses`, `PATCH .../default`.

### Discovery & Search

Search endpoint, sort/filter params (`cuisine`, `price_range`, `open_now`).

### User Engagement

Reviews (`POST /api/v1/orders/{id}/reviews`), Favourites toggle.

### Real-Time Updates

Real-time tracking (`GET /api/v1/orders/{id}/tracking`).
