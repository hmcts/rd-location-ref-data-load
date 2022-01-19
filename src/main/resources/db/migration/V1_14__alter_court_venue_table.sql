ALTER TABLE court_venue ADD COLUMN Welsh_Venue_Name varchar(256);
ALTER TABLE court_venue ADD COLUMN Is_Temporary_Location varchar(1);
ALTER TABLE court_venue ADD COLUMN Is_Nightingale_Court varchar(1);
ALTER TABLE court_venue ADD COLUMN Location_Type varchar(16);
ALTER TABLE court_venue ADD COLUMN Parent_Location varchar(16);
