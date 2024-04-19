CREATE UNIQUE INDEX service_code_idx ON service (service_code);
CREATE UNIQUE INDEX ccd_case_type_idx ON service_to_ccd_case_type_assoc (ccd_case_type);
