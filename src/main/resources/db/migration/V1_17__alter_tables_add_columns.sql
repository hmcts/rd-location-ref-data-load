ALTER TABLE court_venue ADD COLUMN welsh_court_name VARCHAR(256);
ALTER TABLE court_venue ADD COLUMN uprn VARCHAR(16);
ALTER TABLE court_venue ADD COLUMN venue_ou_code VARCHAR(16);
ALTER TABLE court_venue ADD COLUMN mrd_building_location_id VARCHAR(16);
ALTER TABLE court_venue ADD COLUMN mrd_venue_id VARCHAR(16);
ALTER TABLE court_venue ADD COLUMN service_url VARCHAR(1024);
ALTER TABLE court_venue ADD COLUMN fact_url VARCHAR(1024);
ALTER TABLE court_venue ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE court_venue ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE court_venue ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE building_location ADD COLUMN welsh_building_location_name VARCHAR(256);
ALTER TABLE building_location ADD COLUMN welsh_address VARCHAR(512);
ALTER TABLE building_location ADD COLUMN uprn VARCHAR(16);
ALTER TABLE building_location ADD COLUMN latitude NUMERIC;
ALTER TABLE building_location ADD COLUMN longitude NUMERIC;
ALTER TABLE building_location ADD COLUMN mrd_building_location_id VARCHAR(16);
ALTER TABLE building_location ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE building_location ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE building_location ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE court_type ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE court_type ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE court_type ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE region ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE region ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE region ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE cluster ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE cluster ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE cluster ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE court_type_service_assoc ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE court_type_service_assoc ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE court_type_service_assoc ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE district_civil_jurisdiction ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE district_civil_jurisdiction ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE district_civil_jurisdiction ADD COLUMN mrd_deleted_time TIMESTAMP;

ALTER TABLE district_family_jurisdiction ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE district_family_jurisdiction ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE district_family_jurisdiction ADD COLUMN mrd_deleted_time TIMESTAMP;
