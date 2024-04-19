

insert into
  building_location(
    building_location_id,
    region_id,
    building_location_status,
    cluster_id,
    epimms_id,
    building_location_name,
    area,
    court_finder_url,
    postcode,
    address,
    updated_time)
values(
  '13',
  '2',
  'OPEN',
  '9',
  '219165',
  'Building Location B',
  'NORTH',
  'Court Finder URL 2',
  'SW19 2YZ',
  '2 Street, London',
  now())
;
insert into court_venue (court_venue_id, epimms_id, site_name, created_time, updated_time, region_id, court_type_id, cluster_id, open_for_public, court_address, postcode, phone_number, closed_date, court_location_code, dx_address, welsh_site_name, welsh_court_address, court_status, court_open_date, court_name, is_case_management_location, is_hearing_location, is_temporary_location, location_type)
values(2, '815833','Aberdeen Tribunal Hearing Centre 11',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 11', 'Y', 'Y', 'N', 'CTSC'),
(3, '815833','Aberdeen Tribunal Hearing Centre 112',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Closed', null,'ABERDEEN TRIBUNAL HEARING CENTRE 11', 'N', 'Y', 'N', 'NBC'),
(4, '219164','Aberdeen Tribunal Hearing Centre 11',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Open', null,'ABERDEEN TRIBUNAL HEARING CENTRE 11', 'Y', 'N', 'N', 'CTSC'),
(5, '219164','Aberdeen Tribunal Hearing Centre 112',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Closed', null,'ABERDEEN TRIBUNAL HEARING CENTRE 11', 'Y', 'N', 'N', 'NBC'),
(6, '219165','Aberdeen Tribunal Hearing Centre',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Closed', null,'ABERDEEN TRIBUNAL HEARING CENTRE', 'Y', 'Y', 'Y', 'CTSC'),
(7, '219165','Aberdeen Tribunal Hearing Centre',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','2', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Closed', null,'ABERDEEN TRIBUNAL HEARING CENTRE', 'Y', 'N', 'Y', 'CCBC'),
(8, '219165','Aberdeen Tribunal Hearing Centre',	'2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','4', '8',true,'AB10, 57 HUNTLY STREET, ABERDEEN','AB11 5QA',null,null,null,null,null,null,'Closed', null,'ABERDEEN TRIBUNAL HEARING CENTRE', 'Y', 'Y', 'Y', 'CCBC'),
(9, '815833','Manchester County and Family Court', '2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','1','8',true,'BRIDGE STREET WEST','M60 9DJ',null,null,null,null,null,null,'Open', null,'MANCHESTER COUNTY AND FAMILY COURT', 'Y', 'Y', 'N', 'CTSC'),
(10, '815833','Arnhem House (Leicester Offices) Floor 1, 2, 5', '2021-07-05 09:50:46.269032','2021-07-05 09:50:46.269032','7','23','8',true,'WATERLOO WAY','LE1 6LR',null,null,null,null,null,null,'Open', null,'ARNHEM HOUSE (LEICESTER OFFICES) FLOOR 1, 2, 5', 'Y', 'Y', 'N', 'NBC');
