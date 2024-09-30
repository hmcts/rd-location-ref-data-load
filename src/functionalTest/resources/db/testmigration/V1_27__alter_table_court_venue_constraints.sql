alter table court_venue drop CONSTRAINT court_location_uq;

alter table court_venue add CONSTRAINT court_location_unique UNIQUE (epimms_id,court_type_id)
