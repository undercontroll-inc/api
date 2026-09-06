# Breaking API changes

This document lists every contract change from the REST semantics refactor. Use it to update consumers (especially `frontend`). Frontend call sites already identified:

- `frontend/src/services/UserService.js`
- `frontend/src/services/RepairService.js`

Bruno: `Bruno Collection/`. Yaak: `Yaak Collection/` (File → Open Workspace).

## Status codes (all resources)

| Situation | Old | New |
|---|---|---|
| `DELETE` success | `200` on most resources (`204` only on orders) | Always `204 No Content` |
| `GET` collection with no results | `204` on users, order-items, components, demands | Always `200` with `[]` |
| Single-resource `GET` missing | `404` (unchanged), except latest announcement used `204` | Always `404` |

Error responses now always include a stable `code` field (for example `USER_NOT_FOUND`, `VALIDATION_ERROR`, `INTERNAL_SERVER_ERROR`). Validation failures (`400`) also include `errors: [{ field, message }]`. Messages are in English. `timestamp` is now UTC (`ZoneOffset.UTC`).

---

## CSRF and CORS (every browser request)

These apply on localhost too (`http://localhost:3000` / `5173` talking to `http://localhost:8080`). Different ports are different origins.

### CSRF (not required on `/v1/api/**`)

The REST API (`/v1/api/**`) does not require CSRF. Auth is `Authorization: Bearer` plus refresh in the JSON body. There is no session cookie, so the browser does not send credentials on its own; CORS already blocks cross-site reads of the response.

Do not send `X-XSRF-TOKEN`. Do not call `GET /v1/api/auth` (that warmup endpoint does not exist). Axios should omit `withCredentials` (or set it to `false`).

### CORS (stricter)

Old: `applyPermitDefaultValues()` allowed any origin (`*`), plus `TRACE` / `HEAD`.

New:

- Allowed origins come from `undercontroll.cors.allowed-origins` (`CORS_ALLOWED_ORIGINS`). Defaults: `http://localhost:3000`, `http://localhost:5173`. Production **must** set the real frontend origin.
- Methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` only.
- Request headers allowed: `Authorization`, `Content-Type`, `Accept`. Extra custom headers will fail preflight.
- Exposed response headers: `Content-Type`.
- `Access-Control-Allow-Credentials: false` — there is no auth cookie.

---

## Auth (was under Users)

| Old | New | Notes |
|---|---|---|
| `POST /v1/api/users/auth` | `POST /v1/api/auth` | Body now includes `provider`. Password login: `{ "provider": "PASSWORD", "email", "password" }`. **Frontend:** `UserService.auth` |
| `POST /v1/api/users/auth/google` | `POST /v1/api/auth` | Same path as password login. Body: `{ "provider": "GOOGLE", "email", "token" }`. Google ID token is verified with Firebase. **Frontend:** `UserService.googleAuth` |
| `POST /v1/api/users/auth/refresh` | `POST /v1/api/auth/refresh` | Same body (`RefreshTokenRequest`). Login and refresh both return `accessToken` (login no longer uses `token`). |

Login response is now `{ "accessToken", "refreshToken", "user" }`. Refresh remains `{ "accessToken", "refreshToken" }`. Persist `accessToken` from both.

Expired access JWTs return `401` with `code=TOKEN_EXPIRED` and **do not** abort `POST /v1/api/auth/refresh`. Clients should retry once after refresh; a reused refresh token returns `401` with `code=REFRESH_TOKEN_REUSED` and invalidates every session for that user.

`POST /v1/api/auth/login` and `POST /v1/api/auth/google` no longer exist.

`AuthProvider` is `PASSWORD` or `GOOGLE` (`INTERNAL` is not a value). `password` is required for `PASSWORD`; `token` is required for `GOOGLE`.

Rate limiting for login now applies to `/v1/api/auth` and `/v1/api/auth/refresh`.

---

## Users

| Old | New | Notes |
|---|---|---|
| `GET /v1/api/users` | `GET /v1/api/users` | Unchanged path. Empty list is now `200 []` instead of `204`. |
| `GET /v1/api/users/customers` | `GET /v1/api/users?type=CUSTOMER` | **ADMIN only** (same as listing users). Previously any authenticated caller could hit `/customers` via the fallback matcher. |
| `GET /v1/api/users/customers/emails` | `GET /v1/api/users?type=CUSTOMER&hasEmail=true` | ADMIN only. |
| `GET /v1/api/users/customers/{customerId}` | `GET /v1/api/users/{userId}` | Same `UserDto`. |
| `PUT /v1/api/users/{userId}` | `PATCH /v1/api/users/{userId}` | Partial update. **Frontend:** `UserService.updateUser`. |
| `PATCH /v1/api/users/reset-password/{userId}` | `PATCH /v1/api/users/{userId}/password` | Same body (`ResetPasswordRequest`). **Frontend:** `UserService.resetPassword`. |
| `DELETE /v1/api/users/{userId}` | same path | Status `200` → `204`. |

---

## Orders

| Old | New | Notes |
|---|---|---|
| `GET /v1/api/orders?page=&size=` | `GET /v1/api/orders?userId=&page=&size=` | Listing all orders is still admin-only in practice: customers **must** pass their own `userId` or the API returns `403`. |
| `GET /v1/api/orders/filter?userId=` | `GET /v1/api/orders?userId={id}` | **Frontend:** `RepairService.getUserRepairs`. Response is now the paginated `GetAllOrdersResponse` (`data`, `totalElements`, `totalPages`, `page`, `size`), not the old `GetOrdersByUserIdResponse`. |
| `PATCH /v1/api/orders/{id}` | `PATCH /v1/api/orders/{orderId}` | Path variable renamed only. **Frontend:** `RepairService.patchRepair` already uses `/orders/{id}`. |
| `GET /v1/api/orders/export/{orderId}` | `GET /v1/api/orders/{orderId}/export` | **Frontend:** `RepairService.exportOrder`. Filename header is now `report.pdf`. |
| `DELETE /v1/api/orders/{orderId}` | same path | Already `204`. |

`GET /v1/api/orders` is now allowed for `CUSTOMER` **and** `ADMINISTRATOR` (was admin-only). Customers can only request their own `userId`.

---

## Order items (nested under orders)

| Old | New | Notes |
|---|---|---|
| `POST /v1/api/order-items` | `POST /v1/api/orders/{orderId}/items` | `orderId` is in the path, not the body. |
| `GET /v1/api/order-items` | `GET /v1/api/orders/{orderId}/items` | Now scoped to one order. Empty list is `200 []`. |
| `GET /v1/api/order-items/{orderItemId}` | `GET /v1/api/orders/{orderId}/items/{orderItemId}` | `404` if the item does not belong to that order. |
| `PUT /v1/api/order-items` (id in body) | `PATCH /v1/api/orders/{orderId}/items/{orderItemId}` | `id` removed from `UpdateOrderItemRequest`. |
| `DELETE /v1/api/order-items/{orderItemId}` | `DELETE /v1/api/orders/{orderId}/items/{orderItemId}` | Status `200` → `204`. **Frontend:** `RepairService.deleteOrderItem`. |

---

## Demands (nested under orders)

| Old | New | Notes |
|---|---|---|
| `POST /v1/api/demands` | `POST /v1/api/orders/{orderId}/demands` | `orderId` removed from `CreateDemandRequest`. Body is now `{ componentPartId, quantity }`. |
| `GET /v1/api/demands/order/{orderId}` | `GET /v1/api/orders/{orderId}/demands` | Empty list is `200 []`. |
| `GET /v1/api/demands/order/{orderId}/component/{componentId}` | `GET /v1/api/orders/{orderId}/demands?componentId=` | Always returns a **list** (`200`, 0 or 1 item). No longer `200` single object / `404`. |
| `DELETE /v1/api/demands/{demandId}` | `DELETE /v1/api/orders/{orderId}/demands/{demandId}` | Status `200` → `204`. |
| `DELETE /v1/api/demands/order/{orderId}` | `DELETE /v1/api/orders/{orderId}/demands` | Bulk delete of the collection. Status `200` → `204`. |

---

## Components

| Old | New | Notes |
|---|---|---|
| `GET /v1/api/components` | same path | Empty list is `200 []`. |
| `GET /v1/api/components/category/{category}` | `GET /v1/api/components?category=` | |
| `GET /v1/api/components/name/{name}` | `GET /v1/api/components?name=` | Both filters can be combined. |
| `DELETE /v1/api/components/{componentId}` | same path | Status `200` → `204`. |

---

## Announcements

| Old | New | Notes |
|---|---|---|
| `GET /v1/api/announcements/last` | `GET /v1/api/announcements/latest` | Missing announcement is now `404` (was `204`). |
| `DELETE /v1/api/announcements/{announcementId}` | same path | Status `200` → `204`. |

---

## Error body

Old (often without `code`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/v1/api/...",
  "timestamp": "2025-11-23T14:30:00"
}
```

New:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/v1/api/components",
  "timestamp": "2025-11-23T14:30:00",
  "code": "VALIDATION_ERROR",
  "errors": [
    { "field": "item", "message": "must not be blank" }
  ]
}
```

`errors` is omitted when there are no field-level validation failures. Unexpected exceptions now also use this envelope with `code=INTERNAL_SERVER_ERROR` instead of Spring Boot's default error JSON.

---

## Frontend checklist

Cross-cutting:

1. `apiClient` in `frontend/src/providers/api.js`: do **not** set `withCredentials`. Do **not** send `X-XSRF-TOKEN` or warm CSRF with `GET /auth`.
2. Keep sending `Authorization: Bearer …` as today. On `401`, refresh via `POST /auth/refresh` and retry with the new access token. Add nothing else unless you introduce custom headers (CORS will reject them).

Update these calls first:

1. `UserService.auth` → `POST /auth` with `{ provider: "PASSWORD", email, password }`. Persist `accessToken` (not `token`).
2. `UserService.googleAuth` → same `POST /auth` with `{ provider: "GOOGLE", email, token }`. Persist `accessToken` on `authToken`.
3. `UserService.updateUser` → `PATCH /users/{id}` (not `PUT`).
4. `UserService.resetPassword` → `PATCH /users/{userId}/password`.
5. `RepairService.getUserRepairs` → `GET /orders?userId={id}` and read `response.data.data` (paginated wrapper).
6. `RepairService.exportOrder` → `GET /orders/{id}/export`.
7. `RepairService.deleteOrderItem` → `DELETE /orders/{orderId}/items/{id}` (needs the parent order id).

Also stop treating empty `GET` collections as HTTP `204`.
