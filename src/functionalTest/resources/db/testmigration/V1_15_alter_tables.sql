ALTER TABLE court_type ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE court_type ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE court_type ADD COLUMN MRD_Deleted_Time TIMESTAMP;

ALTER TABLE region ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE region ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE region ADD COLUMN MRD_Deleted_Time TIMESTAMP;

ALTER TABLE cluster ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE cluster ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE cluster ADD COLUMN MRD_Deleted_Time TIMESTAMP;

ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE court_type_service_assoc ADD COLUMN MRD_Deleted_Time TIMESTAMP;

ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE district_civil_jurisdiction ADD COLUMN MRD_Deleted_Time TIMESTAMP;

ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Created_Time TIMESTAMP;
ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Updated_Time TIMESTAMP;
ALTER TABLE district_family_jurisdiction ADD COLUMN MRD_Deleted_Time TIMESTAMP;
