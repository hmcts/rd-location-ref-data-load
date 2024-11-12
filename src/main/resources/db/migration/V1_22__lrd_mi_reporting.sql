CREATE SCHEMA IF NOT EXISTS rdlocationreport;

CREATE OR REPLACE VIEW rdlocationreport.vw_org_unit AS
SELECT org_unit_id, description
FROM locrefdata.org_unit;

CREATE OR REPLACE VIEW rdlocationreport.vw_org_business_area AS
SELECT business_area_id, description
FROM locrefdata.org_business_area;

CREATE OR REPLACE VIEW rdlocationreport.vw_org_sub_business_area AS
SELECT sub_business_area_id, description
FROM locrefdata.org_sub_business_area;

CREATE OR REPLACE VIEW rdlocationreport.vw_jurisdiction AS
SELECT jurisdiction_id, description
FROM locrefdata.jurisdiction;

CREATE OR REPLACE VIEW rdlocationreport.vw_service AS
SELECT service_id, org_unit_id, business_area_id, sub_business_area_id, jurisdiction_id,service_code,service_description, service_short_description
FROM locrefdata.service;

CREATE OR REPLACE VIEW rdlocationreport.vw_region AS
SELECT region_id, description
FROM locrefdata.region;

