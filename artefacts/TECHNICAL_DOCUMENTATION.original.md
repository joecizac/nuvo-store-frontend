# Technical Documentation: Nuvo Store Online Delivery Platform

## 1. Architecture and Implementation

### Foundation: Clean Architecture & MVI

The project follows **Clean Architecture** principles, separating concerns into distinct layers to
ensure testability, maintainability, and scalability. This is combined with the **MVI (
Model-View-Intent)** pattern for the presentation layer, providing a unidirectional data flow.

* **Model (State):** A single source of truth for the UI state.
* **View (Composable):** Observes the State and emits Intents.
* **Intent (Action):** Represents user actions or system events that trigger state changes.
* **Effect (Side-Effect):** Handles one-time events like Snackbars, Toasts, or Navigation.

### Module Structure

The project is divided into a feature-driven multi-module structure to enable parallel development
and maintain clean boundaries:

* **`:composeApp`:** The entry point for Android and iOS. It manages the global navigation graph and
  Koin initialization.
* **`:core:mvi`:** Contains the base classes (`BaseViewModel`, `MviContract`) and shared utilities
  like `NumberUtils`. It provides the standardized foundation for all features.
* **`:core:network`:** A shared Ktor-based networking layer with global interceptors (e.g., JWT
  Auth) and base response models.
* **`:core:designsystem`:** A centralized Material 3 Expressive theme, including shared components
  like `StoreCard` and `ProductCard` to ensure visual consistency.
* **`:domain`:** The "pure" layer containing entities (`Store`, `Product`, `CartItem`), repository
  interfaces, and custom exceptions. It has zero dependencies on other modules.
* **`:data`:** Implementation of repository interfaces. It manages data sources including the **Room
  KMP** database for local persistence and Ktor for remote API calls.
* **`:feature:*`:** Encapsulated modules for specific functionalities (Auth, Discovery, Catalog,
  Cart, Checkout). Each feature contains its own MVI components and UI logic.

---

## 2. Technical Decisions and Explanation

### Room KMP with KSP for Local-First Cart

* **Decision:** Chose Room KMP (Kotlin Multiplatform) for the cart instead of SQLDelight or
  In-Memory storage.
* **Rationale:** Room provides a familiar API with powerful features like `Flow` support and
  type-safety. Using KSP allows for efficient code generation across both Android and iOS targets.
* **Impact:** It enables a robust "offline-first" experience where users can manage their cart
  without a network connection, with background synchronization handling server updates.

### Cents-Based Currency System (`Long`)

* **Decision:** Representing all prices as `Long` (cents) internally instead of `Double`.
* **Rationale:** `Double` and `Float` are prone to floating-point precision errors (e.g.,
  `0.1 + 0.2 != 0.3`).
* **Impact:** Ensures 100% accuracy for financial calculations across the app, especially in total
  calculations and checkout summaries.

### Debounced Remote Synchronization

* **Decision:** Implemented a 2-second debounce on the cart synchronization trigger.
* **Rationale:** Users often tap "+" or "-" buttons rapidly. Syncing every single click would result
  in excessive API overhead and potential race conditions.
* **Impact:** Improves performance and reduces server load by batching rapid UI updates into a
  single network request after the user stops interacting.

### Standardized MVI Side-Effects

* **Decision:** Explicitly separated `State` (persistent) from `Effect` (transient).
* **Rationale:** UI events like showing a "Product Added" Snackbar should not be part of the
  persistent state to avoid re-triggering on configuration changes (e.g., rotation).
* **Impact:** Results in a cleaner UI implementation where navigation and messaging are handled
  through a dedicated, collected flow.

---

## 3. Technical Scope for Improvement

### Resource Management Migration

While basic resource folders exist, the project currently relies heavily on hardcoded strings.
Migrating to `jetbrains.compose.resources` is required to follow CMP industry standards, enabling
localized strings and multi-density image support via the type-safe `Res` object.

### Logging and Observability

The current implementation uses `println` for error logging in repositories. Integrating a dedicated
multiplatform logger (like **Kermit** or **Napier**) would provide better visibility into production
issues across different platforms and log levels.

### Pagination and Lazy Loading

Currently, store and product lists are fetched in full. For a production-grade app, implementing the
**Paging 3 KMP** library is essential to handle large catalogs without memory degradation.

### Testing Depth

While ViewModel and Repository unit tests are implemented, the project lacks **UI Tests (Compose
Tests)** and **Integration Tests** that verify the end-to-end flow between features (e.g., checking
if the Cart updates correctly after adding an item from the Catalog).

---

## 4. Required API Endpoints

To reach full e-commerce functionality, the following endpoints are needed beyond the current mock
implementations:

### Address Management

* `POST /api/v1/users/me/addresses`: Create a new delivery address.
* `PUT /api/v1/users/me/addresses/{id}`: Edit an existing address.
* `DELETE /api/v1/users/me/addresses/{id}`: Remove an address.
* `PATCH /api/v1/users/me/addresses/{id}/default`: Set an address as the primary delivery location.

### Advanced Discovery & Search

* `GET /api/v1/stores/search?q={query}`: Global search for stores or specific items.
* `GET /api/v1/stores?sort={rating|distance|delivery_fee}`: Sorting capabilities for discovery.
* `GET /api/v1/stores?filter={cuisine|price_range|open_now}`: Attribute-based filtering.

### User Engagement

* `POST /api/v1/orders/{id}/reviews`: Submit rating and text review for a completed order.
* `GET /api/v1/stores/{id}/reviews`: Fetch paginated user reviews for a specific store.
* `POST /api/v1/users/me/favourites/{storeId|productId}`: Toggle favourites for quick access.

### Real-Time Updates

* `GET /api/v1/orders/{id}/tracking`: Fetch real-time coordinates of the delivery courier (essential
  for Phase 5 polish).
