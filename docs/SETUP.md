# Getting Started

## Prerequisites

1. **Java 21**
2. **Docker Desktop**
3. **Git**

Node.js does not need to be installed separately. Gradle downloads the configured Node.js 20.19.1
runtime and npm dependencies when the frontend build runs for the first time.

## Step 1: Clone the Project

```bash
git clone https://gitlab.com/kdg-ti/programming-5/projects-25-26/acs202/pawel.ryfiak/spring-backend.git
cd spring-backend
```

## Step 2: Start the Database

```bash
docker compose up -d
```

This starts the development PostgreSQL database on port 9432 and the isolated test PostgreSQL database on port 9433.

## Step 3: Build the Project

**Windows:**
```powershell
.\gradlew.bat build
```

**Mac/Linux:**
```bash
./gradlew build
```

The Gradle build installs frontend dependencies and creates the webpack bundles automatically.
It also runs the test suite, so the test database on port 9433 must be available. The application
itself uses the development database on port 9432 when you run it.

## Step 4: Run the Application

**Windows:**
```powershell
.\gradlew.bat bootRun
```

**Mac/Linux:**
```bash
./gradlew bootRun
```

## Step 5: Access the Application

Open your browser and go to: **http://localhost:8080**

---

## Testing

Run the complete Gradle verification lifecycle:

**Windows:**
```powershell
.\gradlew.bat check
```

**Mac/Linux:**
```bash
./gradlew check
```

The test profile uses the PostgreSQL service on port 9433. The HTML report is written to
`build/reports/tests/test/index.html`.

---

## Database Configuration

### PostgreSQL (via Docker)

**Connection Parameters:**
- **URL:** `jdbc:postgresql://localhost:9432/programming5`
- **Username:** `student`
- **Password:** `Student_1234`

**JPA Configuration:**
- `spring.jpa.open-in-view=false` — Open Session in View disabled
- `spring.jpa.hibernate.ddl-auto=create` — Code-first schema generation
- `spring.sql.init.mode=always` — Data seeding from `data.sql`
- SQL logging enabled for query verification

### PostgreSQL Test Database (via Docker)

Automated tests use a separate database so their `create-drop` schema lifecycle cannot remove development data.

**Connection Parameters:**
- **URL:** `jdbc:postgresql://localhost:9433/programming5_test`
- **Username:** `student`
- **Password:** `Student_1234`

In GitLab CI, `CI_DB_HOST_PORT=postgres:5432` redirects the same test profile to the PostgreSQL
service container.

