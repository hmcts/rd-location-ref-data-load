delete from locrefdata.court_venue where service_code = 'AAA1';

delete from locrefdata.court_type_service_assoc where service_code = 'AAA1';

delete from locrefdata.service_to_ccd_case_type_assoc where service_code = 'AAA1';

delete from locrefdata.service where service_code = 'AAA1' and service_description = 'Civil Enforcement';
