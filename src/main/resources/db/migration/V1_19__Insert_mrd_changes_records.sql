truncate table court_type_service_assoc ;

  update cluster set mrd_created_time='2022-04-01 02:00:00',mrd_updated_time='2022-04-01 02:00:00';
  update court_type set mrd_created_time='2022-04-01 02:00:00',mrd_updated_time='2022-04-01 02:00:00';
  update court_type_service_assoc set mrd_created_time='2022-04-01 02:00:00',mrd_updated_time='2022-04-01 02:00:00';

 INSERT INTO court_type_service_assoc (court_type_service_assoc_id,service_code, court_type_id, mrd_created_time, mrd_updated_time, mrd_deleted_time) VALUES
 (1,'AAA1', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (2,'AAA2', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (3,'AAA3', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (4,'AAA4', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (5,'AAA5', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (6,'AAA6', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (7,'AAA7', '10', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (8,'ABA1', '18', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (9,'ABA2', '18', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (10,'ABA3', '18', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (11,'ABA5', '18', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (12,'ABA6', '27', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (13,'ABA7', '18', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (14,'BBA1', '4', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (15,'BBA2', '14', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (16,'BBA3', '31', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (17,'BEA1', '40', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (18,'BFA1', '23', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (19,'BGA1', '3', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (20,'BGA3', '30', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (21,'BHA1', '17', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (22,'BHA2', '19', '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL);

 INSERT INTO region (region_id, description, welsh_description, mrd_created_time, mrd_updated_time, mrd_deleted_time) VALUES
 (10, 'Northern Ireland', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL);
 update region set api_enabled = 'N' where description = 'National';
update region set api_enabled = 'Y' where description != 'National';
update region set mrd_created_time='2022-04-01 02:00:00',mrd_updated_time='2022-04-01 02:00:00';
 COMMIT;
