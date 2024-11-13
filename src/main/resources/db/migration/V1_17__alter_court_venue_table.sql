ALTER TABLE court_venue ADD COLUMN welsh_venue_name varchar(256);
ALTER TABLE court_venue ADD COLUMN is_temporary_location varchar(1);
ALTER TABLE court_venue ADD COLUMN is_nightingale_court varchar(1);
ALTER TABLE court_venue ADD COLUMN location_type varchar(16);
ALTER TABLE court_venue ADD COLUMN parent_location varchar(16);
