ALTER TABLE court_venue ADD COLUMN venue_name varchar(256);
ALTER TABLE court_venue ADD COLUMN is_case_management_location varchar(1);
ALTER TABLE court_venue ADD COLUMN is_hearing_location varchar(1);
