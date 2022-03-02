ALTER TABLE building_location ADD COLUMN welsh_building_location_name VARCHAR(256);
ALTER TABLE building_location ADD COLUMN welsh_address VARCHAR(512);
ALTER TABLE building_location ADD COLUMN uprn VARCHAR(16);
ALTER TABLE building_location ADD COLUMN latitude DECIMAL(10,8);
ALTER TABLE building_location ADD COLUMN longitude DECIMAL(11,8);
ALTER TABLE building_location ADD COLUMN mrd_building_location_id VARCHAR(16);
ALTER TABLE building_location ADD COLUMN mrd_created_time TIMESTAMP;
ALTER TABLE building_location ADD COLUMN mrd_updated_time TIMESTAMP;
ALTER TABLE building_location ADD COLUMN mrd_deleted_time TIMESTAMP;
