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
- **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Database:** PostgreSQL (via Docker)
- **ORM:** Hibernate 7
- **Mapping:** MapStruct 1.6.3
- **Validation:** Jakarta Validation API

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
│   ├── controller/               # MVC Controllers
│   │   └── api/                  # REST Controllers + DTOs + Mapper
│   ├── repository/               # Spring Data JPA Repositories
│   └── enums/                    # Enumerations
└── resources/
    ├── templates/                # Thymeleaf templates
    ├── static/
    │   └── js/                   # JavaScript (fetch API, page logic)
    ├── application.properties    # Application configuration
    ├── data.sql                  # Data seeding
    ├── messages.properties       # i18n (English)
    └── messages_pl.properties    # i18n (Polish)
```

