# Domain Model

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

