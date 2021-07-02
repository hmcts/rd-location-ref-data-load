truncate COURT_VENUE cascade;
truncate BUILDING_LOCATION cascade;
truncate DATALOAD_SCHEDULAR_AUDIT;
truncate DATALOAD_EXCEPTION_RECORDS;
insert into BUILDING_LOCATION (address,building_location_name,epimms_id,postcode) VALUES ('some address', 'aberdeen', '123456', 'ABC 123');
commit;
