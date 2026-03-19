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

### Users & roles

| Username | Password   | Role    |
|----------|------------|---------|
| `admin`  | `admin123` | ADMIN   |
| `user1`  | `user123`  | USER    |
| `user2`  | `user123`  | USER    |

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
- Ajax calls to the REST API send the CSRF token using the `X-XSRF-TOKEN` header.

**Last Updated:** March 8, 2026
