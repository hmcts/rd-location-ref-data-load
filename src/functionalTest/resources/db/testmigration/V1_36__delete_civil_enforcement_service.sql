delete from court_venue where service_code = 'AAA1';

delete from court_type_service_assoc where service_code = 'AAA1';

delete from service_to_ccd_case_type_assoc where service_code = 'AAA1';

delete from service where service_code = 'AAA1' and service_description = 'Civil Enforcement';
