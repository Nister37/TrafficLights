# Architecture

## Three-Layer Architecture

```
┌──────────────────────────────────────┐
│   Presentation Layer (Controller)    │  ← Spring MVC Controllers + REST Controllers
├──────────────────────────────────────┤
│   Business Layer (Service)           │  ← Business Logic + @Transactional
├──────────────────────────────────────┤
│   Data Access Layer (Repository)     │  ← Spring Data JPA (JpaRepository)
└──────────────────────────────────────┘
```

## Key Technologies
- **Backend:** Spring Boot 4.0.2, Spring MVC, Spring Data JPA
- **Frontend:** Thymeleaf, npm, webpack, Bootstrap 5, Bootstrap Icons
- **Database:** PostgreSQL (via Docker)
- **ORM:** Hibernate 7
- **Mapping:** MapStruct 1.6.3
- **Validation:** Jakarta Validation API
- **Week 12:** Spring caching and asynchronous CSV import

## Project Structure

```
src/main/
├── java/be/kdg/programming5/
│   ├── business/
│   │   ├── domain/               # Entity classes
│   │   │   ├── ApplicationUser.java
│   │   │   ├── TrafficLight.java
│   │   │   ├── SmartTrafficLight.java
│   │   │   ├── PedestrianTrafficLight.java
│   │   │   ├── Intersection.java
│   │   │   ├── MaintenanceLog.java
│   │   │   ├── MaintenanceCompany.java
│   │   │   ├── MaintenanceLogCompany.java
│   │   │   └── UserRole.java
│   │   └── services/             # Business logic layer
│   ├── config/                   # MVC, CORS and security configuration
│   ├── controller/
│   │   ├── api/                  # REST Controllers + DTOs + Mapper
│   │   └── mvc/                  # Thymeleaf MVC Controllers
│   ├── exception/                # Shared MVC and API exception handling
│   ├── presentation/             # View models and request-value converters
│   ├── repository/               # Spring Data JPA Repositories
│   └── enums/                    # Enumerations
├── js/                           # Frontend JavaScript source
├── scss/                         # Frontend SCSS source
└── resources/
    ├── templates/                # Thymeleaf templates
    ├── static/                   # Generated bundles/fonts and static images
    ├── application.properties    # Application configuration
    ├── data.sql                  # Data seeding
    ├── messages.properties       # i18n (English)
    └── messages_pl.properties    # i18n (Polish)
```

## Frontend Build

Frontend source files live under `src/main/js` and `src/main/scss`. Webpack bundles them into
`src/main/resources/static`. The Gradle `processResources` task depends on `npm_run_build`, so
`assemble`, `build`, `check` and `bootRun` create current frontend bundles automatically.
Run `./gradlew npm_run_build` when only the frontend bundles need to be rebuilt.

## Security Boundaries

Normal browser-session REST mutations require authentication and CSRF protection. The separate
Week 10 client project uses the dedicated `POST /api/public/maintenance-companies` endpoint, which is the
only CSRF-exempt write path. Public intersection details load read-only cards through
`GET /api/intersections/{id}/traffic-lights`.

CORS for the standalone client origin (`http://localhost:9000`) is limited to
`GET /api/traffic-lights/search` and `POST /api/public/maintenance-companies`.

## Application Features

The `Main` application class enables Spring caching and asynchronous method execution. Search results
are cached in the service layer, while the admin CSV import runs through an `@Async` service method.

