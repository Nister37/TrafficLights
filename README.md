# Traffic Lights Management System

**Author:** Paweł Ryfiak ACS202
**Email:** pawel.ryfiak@student.kdg.be
**Course:** Programming 5

---

## 📚 Documentation

- [Domain Model](docs/DOMAIN.md)
- [Architecture & Project Structure](docs/ARCHITECTURE.md)
- [Setup & Getting Started](docs/SETUP.md)
- [REST API](docs/API.md)

---

## 🌟 Overview

A web application for managing traffic lights, intersections, maintenance schedules, and maintenance companies in an urban traffic control system.

The system allows users to:
- View and manage all traffic lights across the city
- Track intersections and their connected traffic lights
- Schedule and record maintenance activities
- Manage relationships with maintenance companies

---

## 📸 Application Screenshots

### Home Page
![Home Page](docs/resources/demo1.png)

### Traffic Lights Management
![Traffic Lights List](docs/resources/demo2.png)

### Filtering & Details
![Filtering](docs/resources/demo3.png)

---

## Week 2

GET and DELETE REST endpoints for traffic lights. See [REST API — Week 2](docs/API.md#week-2--get--delete-endpoints).

---

## Week 3

POST and PATCH REST endpoints for traffic lights with validation and AJAX integration. See [REST API — Week 3](docs/API.md#week-3--post--patch-endpoints).

---

## Week 4

Spring Security with form-based login, BCrypt password hashing, and content-based authorization.

### Users

| Username | Password  |
|----------|-----------|
| `admin`  | `admin123` |
| `user1`  | `user123` |
| `user2`  | `user123` |

### Pages

- 🌍 **Public page** (accessible by anyone): [Traffic Light Details](http://localhost:8080/trafficLight/1)
  - Anonymous users see basic traffic light info and a teaser prompting login to view the maintenance history.
  - Authenticated users see the full maintenance log section.
- 🔒 **Authenticated page** (login required): [Traffic Lights List](http://localhost:8080/trafficLights)

---

## Week 5

Complex authorization: roles (ADMIN/USER) + ownership-based permissions on traffic lights.

### Role overview (including anonymous)

- **Anonymous (not logged in)**
  - Can visit the landing page and traffic light/intersection details.
  - Cannot create, update or delete traffic lights.
- **USER**
  - Can create new traffic lights (the creator becomes the owner).
  - Can update/delete only traffic lights they own.
- **ADMIN**
  - Can update/delete any traffic light.
  - Can access the admin-only page.

### Verification links

- Public page: [Traffic Light Details](http://localhost:8080/trafficLight/1)
- Admin-only page: [Admin](http://localhost:8080/admin)
- Ownership rules (example): [Traffic Light Details #1](http://localhost:8080/trafficLight/1)
  - The details page shows the traffic light owner.
  - Delete actions are hidden unless you are the owner or an admin.

### CSRF + REST/Ajax

CSRF protection is enabled.

- MVC forms include CSRF tokens automatically.
- Ajax calls to the REST API read the CSRF token + header name from Thymeleaf meta tags and send it as a request header
   (Spring typically uses `X-CSRF-TOKEN`, but the client uses the server-provided header name).

---

## Week 6

Spring profiles for test isolation and integration tests for the repository and service layers.

### Spring profiles

| Profile | Database | Seeding | Purpose |
|---------|----------|---------|---------|
| *(default)* | PostgreSQL (`trafficlights`) | `data.sql` | Development / production |
| `test` | H2 in-memory (`testdb`) | `@BeforeEach` only | Automated tests |

The `test` profile (`application-test.properties`) disables `data.sql` via `spring.sql.init.mode=never` and uses `ddl-auto=create-drop` so every test run starts with a clean schema. Tests seed their own data in `@BeforeEach` and clean up in `@AfterEach`.

### Running tests from the command line

```bash
# Windows
.\gradlew.bat test

# Linux / macOS
./gradlew test
```

Test reports are generated at `build/reports/tests/test/index.html`.

### Test overview

**Repository layer** (`TrafficLightRepositoryTest` — 5 tests):
- Delete cascade: FK constraint prevents orphan deletion; manual child removal allows it
- Nullability: `@Column(nullable = false)` on `status` enforced at DB level
- Lazy/eager loading: `findById` keeps `maintenanceLogs` lazy; `findByIdWithMaintenanceLogs` (JOIN FETCH) loads them eagerly

**Service layer** (`TrafficLightServiceIntegrationTest` — 8 tests):
- `deleteTrafficLight`: owner success, admin success, non-owner forbidden, not-found
- `updateTrafficLight`: owner partial update, admin multi-field update, non-owner forbidden, not-found

---

**Last Updated:** April 2, 2026
