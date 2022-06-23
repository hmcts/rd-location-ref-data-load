--Delete building locations and court venues
TRUNCATE court_venue CASCADE;
TRUNCATE building_location CASCADE;

--Update region ids
INSERT INTO region(region_id, description, created_time, api_enabled, mrd_created_time, mrd_updated_time)
SELECT '12', description, created_time, api_enabled, mrd_created_time, mrd_updated_time
FROM region
WHERE region_id = '1';

UPDATE region
SET description = 'London',
api_enabled = 'Y'
WHERE region_id = '1';

UPDATE region
SET description = 'Midlands'
WHERE region_id = '2';

UPDATE region
SET description = 'North East'
WHERE region_id = '3';

UPDATE region
SET description = 'North West'
WHERE region_id = '4';

UPDATE region
SET description = 'South East'
WHERE region_id = '5';

UPDATE region
SET description = 'South West'
WHERE region_id = '6';

UPDATE region
SET description = 'Wales'
WHERE region_id = '7';

UPDATE region
SET description = 'Northern Ireland',
created_time = now()
WHERE region_id = '10';

UPDATE region
SET region_id = '11'
WHERE description = 'Scotland';

DELETE FROM region
WHERE region_id = '8';

DELETE FROM region
WHERE region_id = '9';

