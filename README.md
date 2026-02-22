# Traffic Lights Management System

**Author:** Paweł Ryfiak ACS202
**Email:** pawel.ryfiak@student.kdg.be  
**Course:** Programming 5

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

## 📋 Domain Model

The system manages four main entities:

### **1. Traffic Light**
- **Attributes:** ID, Status (ACTIVE/MAINTENANCE/BROKEN/PLANNED), Installation Date, Direction, Type (COLLISION/NON_COLLISION), Right Arrow
- **Subclasses:**
    - `SmartTrafficLight`: Includes sensor type and connectivity features
    - `PedestrianTrafficLight`: Includes audio signal and button request capabilities
- **Relationships:**
    - Belongs to one `Intersection` (Many-to-One)
    - Has many `MaintenanceLogs` (One-to-Many)

### **2. Intersection**
- **Attributes:** ID, Latitude, Longitude, Type (CROSSROADS/T_JUNCTION/ROUNDABOUT/COMPLEX), Number of Roads, Smart Enabled, Opened Date, Has Pedestrian Crossing, Image Path
- **Relationships:**
    - Has many `TrafficLights` (One-to-Many)

### **3. Maintenance Log**
- **Attributes:** ID, Date, Description, Kind (ELECTRICAL/MECHANICAL/SOFTWARE/CLEANING), Cost, Completed, Invoice Number
- **Relationships:**
    - Belongs to one `TrafficLight` (Many-to-One)
    - Associated with many `MaintenanceCompanies` (Many-to-Many via `MaintenanceLogCompany` association entity)

### **4. Maintenance Company**
- **Attributes:** ID, Name, Email, Phone Number, Active, Since Date
- **Relationships:**
    - Works on many `MaintenanceLogs` (Many-to-Many via `MaintenanceLogCompany` association entity)

### **5. MaintenanceLogCompany (Association Entity)**
- **Attributes:** ID, Assigned Date
- **Relationships:**
    - References one `MaintenanceLog` (Many-to-One)
    - References one `MaintenanceCompany` (Many-to-One)
- **Constraint:** Unique constraint on (maintenance_log_id, maintenance_company_id)

---

## 🏗️ Architecture

### Three-Layer Architecture

```
┌──────────────────────────────────────┐
│   Presentation Layer (Controller)    │  ← Spring MVC Controllers
├──────────────────────────────────────┤
│   Business Layer (Service)           │  ← Business Logic + @Transactional
├──────────────────────────────────────┤
│   Data Access Layer (Repository)     │  ← Spring Data JPA (JpaRepository)
└──────────────────────────────────────┘
```

### Key Technologies
- **Backend:** Spring Boot 4.0.2, Spring MVC, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Database:** PostgreSQL (via Docker)
- **ORM:** Hibernate 7
- **Validation:** Jakarta Validation API

---

## 🗄️ Database Configuration

### PostgreSQL (via Docker)

**Connection Parameters:**
- **URL:** `jdbc:postgresql://localhost:9432/programming5`
- **Username:** `student`
- **Password:** `Student_1234`

**JPA Configuration:**
- `spring.jpa.open-in-view=false` - Open Session in View disabled
- `spring.jpa.hibernate.ddl-auto=create` - Code-first schema generation
- `spring.sql.init.mode=always` - Data seeding from `data.sql`
- SQL logging enabled for query verification

---

## 🚀 Getting Started

### Prerequisites

1. **Java 21**
2. **Docker Desktop**
3. **Git**

### Step 1: Clone the Project

```bash
git clone <repository-url>
cd Pawel-Ryfiak-Traffic-Lights-2
```

### Step 2: Start the Database

```bash
docker-compose up -d
```

This starts PostgreSQL on port 9432.

### Step 3: Build the Project

**Windows:**
```powershell
.\gradlew.bat build
```

**Mac/Linux:**
```bash
./gradlew build
```

### Step 4: Run the Application

**Windows:**
```powershell
.\gradlew.bat bootRun
```

**Mac/Linux:**
```bash
./gradlew bootRun
```

### Step 5: Access the Application

Open your browser and go to: **http://localhost:8080**

---

## 📁 Project Structure

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
│   ├── repository/               # Spring Data JPA Repositories
│   └── enums/                    # Enumerations
└── resources/
    ├── templates/                # Thymeleaf templates
    ├── static/                   # Static resources (CSS, images)
    ├── application.properties    # Application configuration
    ├── data.sql                  # Data seeding
    ├── messages.properties       # i18n (English)
    └── messages_pl.properties    # i18n (Polish)
```

---

## Week 2

### REST API Endpoints

The application exposes a REST API for traffic light management.

#### GET All Traffic Lights - 200 OK

**Request:**
```http
GET /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "status": "ACTIVE",
    "installationDate": "2020-01-15",
    "direction": "NE",
    "type": "COLLISION",
    "rightArrow": true,
    "intersectionId": 1,
    "category": "SmartTrafficLight"
  },
  {
    "id": 2,
    "status": "BROKEN",
    "installationDate": "2019-05-20",
    "direction": "E",
    "type": "COLLISION",
    "rightArrow": false,
    "intersectionId": 1,
    "category": "TrafficLight"
  }
]
```

#### GET Single Traffic Light - 200 OK

**Request:**
```http
GET /api/traffic-lights/1 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "status": "ACTIVE",
  "installationDate": "2020-01-15",
  "direction": "NE",
  "type": "COLLISION",
  "rightArrow": true,
  "intersectionId": 1,
  "category": "SmartTrafficLight"
}
```

#### GET Single Traffic Light - 404 Not Found

**Request:**
```http
GET /api/traffic-lights/9999 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Traffic light with id 9999 not found"
}
```

#### GET Traffic Lights for Intersection - 200 OK

**Request:**
```http
GET /api/intersections/1/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "status": "ACTIVE",
    "installationDate": "2020-01-15",
    "direction": "NE",
    "type": "COLLISION",
    "rightArrow": true,
    "intersectionId": 1,
    "category": "SmartTrafficLight"
  }
]
```

#### GET Traffic Lights for Intersection - 404 Not Found

**Request:**
```http
GET /api/intersections/9999/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Intersection with id 9999 not found"
}
```

#### DELETE Traffic Light - 204 No Content

**Request:**
```http
DELETE /api/traffic-lights/5 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 204 No Content
```

#### DELETE Traffic Light - 404 Not Found

**Request:**
```http
DELETE /api/traffic-lights/9999 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Traffic light with id 9999 not found"
}
```

---

**Last Updated:** February 22, 2026
