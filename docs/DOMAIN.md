# Domain Model

The core persisted model is described below. `TrafficLight` also has two specialized entity subclasses.

### 1. Traffic Light

- **Attributes:** ID, status (`ACTIVE`/`MAINTENANCE`/`BROKEN`/`PLANNED`), installation date, direction, type (`COLLISION`/`NON_COLLISION`), right arrow
- **Subclasses:**
    - `SmartTrafficLight`: sensor type and connectivity
    - `PedestrianTrafficLight`: audio signal and button-request support
- **Relationships:**
    - Belongs to one `Intersection` (Many-to-One)
    - Has one optional owner (`ApplicationUser`, Many-to-One)
    - Has many `MaintenanceLog` records (One-to-Many)

Traffic-light inheritance uses a single database table with a discriminator column.

### 2. Intersection

- **Attributes:** ID, latitude, longitude, type (`CROSSROADS`/`T_JUNCTION`/`ROUNDABOUT`/`COMPLEX`), road count, smart-enabled flag, opened-on date, pedestrian-crossing flag, intersection image
- **Relationships:**
    - Has many `TrafficLight` records (One-to-Many)

### 3. Maintenance Log

- **Attributes:** ID, date, description, kind (`ELECTRICAL`/`MECHANICAL`/`SOFTWARE`/`CLEANING`), cost, completed flag, invoice number
- **Relationships:**
    - Belongs to one `TrafficLight` (Many-to-One)
    - Has many `MaintenanceLogCompany` association records (One-to-Many)

### 4. Maintenance Company

- **Attributes:** ID, name, contact email, contact phone, active flag, since date
- **Relationships:**
    - Has many `MaintenanceLogCompany` association records (One-to-Many)

### 5. MaintenanceLogCompany

This association entity represents the many-to-many business relationship between maintenance logs
and maintenance companies.

- **Attributes:** ID, assigned date
- **Relationships:**
    - References one `MaintenanceLog` (Many-to-One)
    - References one `MaintenanceCompany` (Many-to-One)
- **Constraint:** Unique pair of `maintenance_log_id` and `maintenance_company_id`

### 6. Application User

- **Attributes:** ID, unique username, BCrypt password hash, role (`USER`/`ADMIN`)
- **Relationships:**
    - Can be referenced as the owner of many `TrafficLight` records; the mapping is stored on `TrafficLight`

