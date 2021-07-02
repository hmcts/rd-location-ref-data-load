truncate court_venue cascade;
truncate dataload_schedular_audit;
truncate dataload_exception_records;
insert into building_location (address,building_location_name,epimms_id,postcode) values ('some address', 'aberdeen', '123456', 'ABC 123')
commit;
