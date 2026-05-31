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
│   │   │   ├── TrafficLight.java
│   │   │   ├── SmartTrafficLight.java
│   │   │   ├── PedestrianTrafficLight.java
│   │   │   ├── Intersection.java
│   │   │   ├── MaintenanceLog.java
│   │   │   ├── MaintenanceCompany.java
│   │   │   └── MaintenanceLogCompany.java
│   │   └── services/             # Business logic layer
│   ├── config/security/          # Spring Security configuration
│   ├── controller/
│   │   ├── api/                  # REST Controllers + DTOs + Mapper
│   │   └── mvc/                  # Thymeleaf MVC Controllers
│   ├── repository/               # Spring Data JPA Repositories
│   └── enums/                    # Enumerations
├── js/                           # Frontend JavaScript source
├── scss/                         # Frontend SCSS source
└── resources/
    ├── templates/                # Thymeleaf templates
    ├── static/                   # Generated webpack bundles
    ├── application.properties    # Application configuration
    ├── data.sql                  # Data seeding
    ├── messages.properties       # i18n (English)
    └── messages_pl.properties    # i18n (Polish)
```

## Frontend Build

Frontend source files live under `src/main/js` and `src/main/scss`. Webpack bundles them into
`src/main/resources/static`, which contains generated browser assets. Run `npm run build` after
changing frontend source files. The Gradle build also invokes the webpack build task.

## Security Boundaries

Normal browser-session REST mutations require authentication and CSRF protection. The separate
Week 10 client project uses the dedicated `POST /api/public/maintenance-companies` endpoint, which is the
only CSRF-exempt write path. Public intersection details load read-only cards through
`GET /api/intersections/{id}/traffic-lights`.

