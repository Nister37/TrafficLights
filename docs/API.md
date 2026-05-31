# REST API

## Authentication and CSRF

The browser-session management API uses Spring Security:

- `GET /api/traffic-lights/search?status=...` and `GET /api/intersections/{id}/traffic-lights` are public read endpoints.
- `GET` and mutation requests under `/api/traffic-lights` require a valid `JSESSIONID` cookie, except for the public search endpoint.
- `POST`, `PATCH` and `DELETE` requests under `/api/traffic-lights` also require the CSRF token in the header name provided by Spring Security, normally `X-CSRF-TOKEN`.
- Updating or deleting a traffic light is limited to its owner or an admin.
- `GET /api/public/traffic-lights` and `POST /api/public/maintenance-companies` support the separate Week 10 client project. Maintenance company creation is the only CSRF-exempt write endpoint.

The examples below use placeholder cookie and CSRF values. See `http/api/traffic-lights-api.http` for directly runnable request templates.

## Public Client Endpoints

The separate Week 10 client project can search traffic lights and create maintenance companies without a browser session:

```http
GET /api/traffic-lights/search?status=ACTIVE HTTP/1.1
Host: localhost:8080
Accept: application/json
```

```http
POST /api/public/maintenance-companies HTTP/1.1
Host: localhost:8080
Accept: application/json
Content-Type: application/json

{
  "name": "Signal Support",
  "contactPhone": "+32 123 45 67",
  "contactEmail": "contact@signalsupport.example",
  "active": true,
  "since": "2024-01-15"
}
```

## Week 2 — GET & DELETE endpoints

---

### GET All Traffic Lights - 200 OK

**Request:**
```http
GET /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=<session-cookie>
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

---

### GET Single Traffic Light - 200 OK

**Request:**
```http
GET /api/traffic-lights/1 HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=<session-cookie>
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

---

### GET Single Traffic Light - 404 Not Found

**Request:**
```http
GET /api/traffic-lights/9999 HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=<session-cookie>
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

### GET Traffic Lights for Intersection - 200 OK

This read-only endpoint is public because the anonymous intersection details page loads
its traffic-light cards through AJAX. Traffic-light modifications still require authentication.

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
    "category": "SmartTrafficLight",
    "ownerUsername": "user1"
  }
]
```

---

### GET Traffic Lights for Intersection - 404 Not Found

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

---

### DELETE Traffic Light - 204 No Content

**Request:**
```http
DELETE /api/traffic-lights/5 HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>
```

**Response:**
```http
HTTP/1.1 204 No Content
```

---

### DELETE Traffic Light - 404 Not Found

**Request:**
```http
DELETE /api/traffic-lights/9999 HTTP/1.1
Host: localhost:8080
Accept: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>
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

## Week 3 — POST & PATCH endpoints

Added POST (create) and PATCH (partial update) endpoints for traffic lights, with server-side validation using Jakarta Validation API (`@NotNull`, `@PastOrPresent`, `@Positive`).

MapStruct is used for trivial entity-to-DTO mappings. Complex mappings (DTO → entity) are handled manually in the service layer — the controller extracts values from the DTO and passes them as individual parameters.

AJAX is used on the intersection details page to add and update traffic lights without a page refresh.

---

### Creating a Traffic Light - Created

**Request:**
```http
POST /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "PLANNED",
  "installationDate": "2024-01-15",
  "direction": "N",
  "type": "COLLISION",
  "rightArrow": false,
  "intersectionId": 1
}
```

**Response:**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 6,
  "status": "PLANNED",
  "installationDate": "2024-01-15",
  "direction": "N",
  "type": "COLLISION",
  "rightArrow": false,
  "intersectionId": 1,
  "category": "TrafficLight"
}
```

---

### Creating a Traffic Light - Bad Request (missing required fields)

**Request:**
```http
POST /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "ACTIVE",
  "rightArrow": true
}
```

**Response:**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "message": "Validation failed: direction: Direction is required, installationDate: Installation date is required, intersectionId: Intersection ID is required, type: Type is required"
}
```

---

### Creating a Traffic Light - Bad Request (future installation date)

**Request:**
```http
POST /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "PLANNED",
  "installationDate": "2030-12-31",
  "direction": "S",
  "type": "NON_COLLISION",
  "rightArrow": false,
  "intersectionId": 1
}
```

**Response:**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "message": "Validation failed: installationDate: Installation date cannot be in the future"
}
```

---

### Creating a Traffic Light - Not Found (non-existent intersection)

**Request:**
```http
POST /api/traffic-lights HTTP/1.1
Host: localhost:8080
Accept: application/json
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "PLANNED",
  "installationDate": "2024-01-15",
  "direction": "E",
  "type": "COLLISION",
  "rightArrow": false,
  "intersectionId": 9999
}
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Intersection with id 9999 not found"
}
```

---

### Updating a Traffic Light - No Content

**Request:**
```http
PATCH /api/traffic-lights/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "MAINTENANCE"
}
```

**Response:**
```http
HTTP/1.1 204 No Content
```

---

### Updating a Traffic Light (multiple fields) - No Content

**Request:**
```http
PATCH /api/traffic-lights/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "ACTIVE",
  "direction": "SW",
  "rightArrow": true
}
```

**Response:**
```http
HTTP/1.1 204 No Content
```

---

### Updating a Traffic Light - Not Found

**Request:**
```http
PATCH /api/traffic-lights/9999 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cookie: JSESSIONID=<session-cookie>
X-CSRF-TOKEN: <csrf-token>

{
  "status": "ACTIVE"
}
```

**Response:**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "message": "Traffic light with id 9999 not found"
}
```

