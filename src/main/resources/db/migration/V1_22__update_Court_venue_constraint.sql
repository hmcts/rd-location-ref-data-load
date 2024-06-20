alter table court_venue drop CONSTRAINT court_location_code_uq;

alter table court_venue add CONSTRAINT court_location_uq UNIQUE (epimms_id,court_type_id);
