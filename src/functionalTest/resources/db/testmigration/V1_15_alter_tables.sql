ALTER TABLE court_type ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE court_type ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE court_type ADD COLUMN MRD_Deleted_Time timestamp;

ALTER TABLE region ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE region ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE region ADD COLUMN MRD_Deleted_Time timestamp;

ALTER TABLE cluster ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE cluster ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE cluster ADD COLUMN MRD_Deleted_Time timestamp;

ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Deleted_Time timestamp;

ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Deleted_Time timestamp;

ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Created_Time timestamp;
ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Updated_Time timestamp;
ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Deleted_Time timestamp;
