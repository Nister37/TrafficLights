-- Insert Intersections (without explicit IDs - let SERIAL auto-generate)
INSERT INTO intersection (latitude, longitude, type, road_count, is_smart_enabled, opened_on, has_pedestrian_crossing, intersection_image) VALUES
(42.6977, 23.3219, 'CROSSROADS', 4, true, '2020-01-15', true, '/images/intersections/intersection1.png'),
(42.6980, 23.3225, 'CROSSROADS', 3, false, '2019-05-20', false, '/images/intersections/intersection2.png'),
(42.6990, 23.3230, 'T_JUNCTION', 4, true, '2021-03-10', true, '/images/intersections/intersection3.png'),
(42.7000, 23.3240, 'ROUNDABOUT', 3, false, '2018-07-25', false, '/images/intersections/intersection4.png'),
(42.7010, 23.3250, 'COMPLEX', 4, true, '2022-11-05', true, '/images/intersections/default.png');

-- Insert Traffic Lights (without explicit IDs - let SERIAL auto-generate)
-- Note: dtype column required for JPA inheritance
-- All columns included for SINGLE_TABLE inheritance: sensor_type, has_connectivity (SmartTrafficLight), has_audio_signal, has_button_request (PedestrianTrafficLight)
INSERT INTO traffic_light (dtype, status, installation_date, direction, type, right_arrow, intersection_id, sensor_type, has_connectivity, has_audio_signal, has_button_request) VALUES
('SmartTrafficLight', 'ACTIVE', '2020-01-15', 'NE', 'COLLISION', true, 1, 'Infrared', true, NULL, NULL),
('TrafficLight', 'BROKEN', '2019-05-20', 'E', 'COLLISION', false, 1, NULL, NULL, NULL, NULL),
('PedestrianTrafficLight', 'MAINTENANCE', '2021-03-10', 'N', 'COLLISION', true, 2, NULL, NULL, true, true),
('TrafficLight', 'PLANNED', '2018-07-25', 'S', 'NON_COLLISION', false, 3, NULL, NULL, NULL, NULL),
('SmartTrafficLight', 'ACTIVE', '2022-11-05', 'SW', 'NON_COLLISION', true, 4, 'Camera', true, NULL, NULL);

-- Insert Maintenance Companies (without explicit IDs - let SERIAL auto-generate)
INSERT INTO maintenance_company (name, contact_phone, contact_email, active, since) VALUES
('Sofia Traffic Solutions', '+359 2 123 4567', 'sofia@traffic.bg', true, '2010-03-15'),
('Urban Signal Systems', '+359 2 234 5678', 'contact@urbansignal.bg', true, '2015-07-20'),
('Bulgarian Traffic Tech', '+359 2 345 6789', 'info@bgtraffic.bg', false, '2012-11-05'),
('Metro Light Services', '+359 2 456 7890', 'service@metrolight.bg', true, '2018-02-10'),
('Smart City Maintenance', '+359 2 567 8901', 'hello@smartcity.bg', true, '2020-09-25');

-- Insert Maintenance Logs (without explicit IDs - let SERIAL auto-generate)
INSERT INTO maintenance_log (date, description, kind, cost, completed, invoice_number, traffic_light_id) VALUES
('2023-01-15', 'LED bulbs replacement', 'ELECTRICAL', 250.50, true, 'INV-2023-001', 1),
('2023-02-20', 'Timer mechanism repair', 'MECHANICAL', 180.75, true, 'INV-2023-002', 1),
('2023-03-10', 'Software update and calibration', 'SOFTWARE', 320.0, false, 'INV-2023-003', 2),
('2023-04-05', 'General cleaning and inspection', 'CLEANING', 95.25, true, 'INV-2023-004', 3),
('2023-05-18', 'Emergency wiring fix', 'ELECTRICAL', 450.0, true, 'INV-2023-005', 4),
('2023-06-22', 'Sensor calibration', 'MECHANICAL', 275.80, false, 'INV-2023-006', 5);

-- Insert Maintenance Log Company relationships (many-to-many via association entity)
-- Now includes assigned_date column per Scenario 1 guidelines
INSERT INTO maintenance_log_company (maintenance_log_id, maintenance_company_id, assigned_date) VALUES
(1, 1, '2023-01-15'),
(1, 2, '2023-01-15'),
(2, 1, '2023-02-20'),
(3, 3, '2023-03-10'),
(3, 5, '2023-03-10'),
(4, 4, '2023-04-05'),
(5, 1, '2023-05-18'),
(5, 4, '2023-05-18'),
(6, 2, '2023-06-22'),
(6, 5, '2023-06-22');

-- Insert application users (BCrypt hashed passwords, strength 10)
-- user / user123
INSERT INTO application_user (username, password_hash) VALUES
('user', '$2a$10$NFAFc4Z7lPAXNpSBM66Jbe9JLmV738kQDYoxdsYRJZLCZZPIlRpsa');

