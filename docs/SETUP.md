# Getting Started

## Prerequisites

1. **Java 21**
2. **Docker Desktop**
3. **Git**

## Step 1: Clone the Project

```bash
git clone <repository-url>
cd Pawel-Ryfiak-Traffic-Lights-2
```

## Step 2: Start the Database

```bash
docker-compose up -d
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

