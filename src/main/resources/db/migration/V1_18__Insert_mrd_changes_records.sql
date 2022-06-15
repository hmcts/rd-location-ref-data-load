truncate table cluster cascade;
 truncate table court_type cascade;
 truncate table court_type_service_assoc cascade;
 truncate table region cascade;


 INSERT INTO cluster (cluster_id,cluster_name,welsh_cluster_name, mrd_created_time,mrd_updated_time,mrd_deleted_time) VALUES
 (1, 'Avon, Somerset and Gloucestershire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (2, 'Bedfordshire, Cambridgeshire, Hertfordshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (3, 'Cheshire and Merseyside', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (4, 'Cleveland, Durham, Northumbria', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (5, 'Cumbria and Lancashire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (6, 'Derbyshire and Nottinghamshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (7, 'Devon, Cornwall, Dorset', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (8, 'Greater Manchester', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (9, 'Hampshire, Wiltshire, IOW', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (10, 'Humber and South Yorkshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (11, 'Kent', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (12, 'Leicestershire, Lincolnshire, Rutland, Northamptonshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (13, 'Norfolk, Essex, Suffolk', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (14, 'North and West Yorkshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (15, 'Staffordshire and West Mercia', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (16, 'Surrey and Sussex', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (17, 'Thames Valley', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (18, 'West Midlands and Warwickshire', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL);

 INSERT INTO court_type (court_type_id, court_type, welsh_court_type, mrd_created_time, mrd_updated_time, mrd_deleted_time) VALUES
 (1, 'Admin Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (2, 'Admiralty and Commercial Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (3, 'Agricultural Land and Drainage Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (4, 'Asylum Support Appeals', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (5, 'Bankruptcy Court (High Court)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (6, 'Business and Property Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (7, 'Central London County Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (8, 'Central London County Court (Bankruptcy)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (9, 'Chancery Division', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (10, 'County Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (11, 'Court of Appeal Civil Division', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (12, 'Court of Appeal Criminal Division', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (13, 'Court of Protection', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (14, 'Criminal Injuries Compensation', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (15, 'Crown Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (16, 'Employment Appeal Tribunal (England and Wales)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (17, 'Employment Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (18, 'Family Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (19, 'Gangmasters Licensing Appeals', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (20, 'General Regulatory Chamber', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (21, 'High Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (22, 'Housing Centre', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (23, 'Immigration and Asylum Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (24, 'Lands Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (25, 'Magistrates Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (26, 'Patents Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (27, 'Probate', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (28, 'Queen''s Bench Division (General)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (29, 'Queen''s Bench Division Administrative Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (30, 'Residential Property Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (31, 'Social Security and Child Support Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (32, 'Special Immigration Appeals Commission', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (33, 'Tax Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (34, 'Technology & Construction Court', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (35, 'Tribunals ', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (36, 'Upper Tier Immigration and Asylum Tribunal', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (37, 'Upper Tribunal (Administrative Appeals Chamber)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (38, 'Upper Tribunal (Lands Chamber)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (39, 'Upper Tribunal (Tax and Chancery Chamber)', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (40, 'War Pensions & Armed Forces Compensation Chamber', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL);


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
 (1, 'National', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (2, 'London', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (3, 'Midlands', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (4, 'North East', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (5, 'North West', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (6, 'South East', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (7, 'South West', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (8, 'Wales', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (9, 'Scotland', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL),
 (10, 'Northern Ireland', NULL, '2022-04-01 02:00:00', '2022-04-01 02:00:00', NULL);

 update region set api_enabled = 'N' where description = 'National';
update region set api_enabled = 'Y' where description != 'National';

 COMMIT;
