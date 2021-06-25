alter table court_location_category alter column welsh_court_location_category drop not null;

insert into court_location_category(
    court_location_category_id,
    court_location_category,
    created_time
    )
values(
    '1',
    'Admin Court',
    now()),
    (
    '2',
    'Admiralty and Commercial Court',
    now()),
    (
    '3',
    'Agricultural Land and Drainage Tribunal',
    now()),
    (
    '4',
    'Asylum Support Appeals',
    now()),
    (
    '5',
    'Bankruptcy Court (High Court)',
    now()),
    (
    '6',
    'Business and Property Court',
    now()),
    (
    '7',
    'Central London County Court',
    now()),
    (
    '8',
    'Central London County Court (Bankruptcy)',
    now()),
    (
    '9',
    'Chancery Division',
    now()),
    (
    '10',
    'County Court',
    now()),
    (
    '11',
    'Court of Appeal Civil Division',
    now()),
    (
    '12',
    'Court of Appeal Criminal Division',
    now()),
    (
    '13',
    'Court of Protection',
    now()),
    (
    '14',
    'Criminal Injuries Compensation',
    now()),
    (
    '15',
    'Crown Court',
    now()),
    (
    '16',
    'Employment Appeal Tribunal (England and Wales)',
    now()),
    (
    '17',
    'Employment Tribunal',
    now()),
    (
    '18',
    'Family Court',
    now()),
    (
    '19',
    'Gangmasters Licensing Appeals',
    now()),
    (
    '20',
    'General Regulatory Chamber',
    now()),
    (
    '21',
    'High Court',
    now()),
    (
    '22',
    'Housing Centre',
    now()),
    (
    '23',
    'Immigration and Asylum Tribunal',
    now()),
    (
    '24',
    'Lands Tribunal',
    now()),
    (
    '25',
    'Magistrates Court',
    now()),
    (
    '26',
    'Patents Court',
    now()),
    (
    '27',
    'Probate',
    now()),
    (
    '28',
    'Queen''s Bench Division (General)',
    now()),
    (
    '29',
    'Queen''s Bench Division Administrative Court',
    now()),
    (
    '30',
    'Residential Property Tribunal',
    now()),
    (
    '31',
    'Social Security and Child Support Tribunal',
    now()),
    (
    '32',
    'Special Immigration Appeals Commission',
    now()),
    (
    '33',
    'Tax Tribunal',
    now()),
    (
    '34',
    'Technology & Construction Court',
    now()),
    (
    '35',
    'Tribunals',
    now()),
    (
    '36',
    'Upper Tier Immigration and Asylum Tribunal',
    now()),
    (
    '37',
    'Upper Tribunal (Administrative Appeals Chamber)',
    now()),
    (
    '38',
    'Upper Tribunal (Lands Chamber)',
    now()),
    (
    '39',
    'Upper Tribunal (Tax and Chancery Chamber)',
    now()),
    (
    '40',
    'War Pensions & Armed Forces Compensation Chamber',
    now())
;

insert into building_location_status(
    building_location_status_id,
    status,
    created_time)
values(
    '1',
    'OPEN',
    now()),
    (
    '2',
    'CLOSED',
    now())
;

insert into region(
    region_id,
    description,
    created_time
    )
values(
    '1',
    'National',
    now()),
    (
    '2',
    'London',
    now()),
    (
    '3',
    'Midlands',
    now()),
    (
    '4',
    'North East',
    now()),
    (
    '5',
    'North West',
    now()),
    (
    '6',
    'South East',
    now()),
    (
    '7',
    'South West',
    now()),
    (
    '8',
    'Wales',
    now()),
    (
    '9',
    'Scotland',
    now())
;

insert into cluster(
    cluster_id,
    cluster_name,
    created_time
    )
values(
    '1',
    'Avon, Somerset and Gloucestershire',
    now()),
    (
    '2',
    'Bedfordshire, Cambridgeshire, Hertfordshire',
    now()),
    (
    '3',
    'Cheshire and Merseyside',
    now()),
    (
    '4',
    'Cleveland, Durham, Northumbria',
    now()),
    (
    '5',
    'Cumbria and Lancashire',
    now()),
    (
    '6',
    'Derbyshire and Nottinghamshire',
    now()),
    (
    '7',
    'Devon, Cornwall, Dorset',
    now()),
    (
    '8',
    'Greater Manchester',
    now()),
    (
    '9',
    'Hampshire, Wiltshire, IOW',
    now()),
    (
    '10',
    'Humber and South Yorkshire',
    now()),
    (
    '11',
    'Kent',
    now()),
    (
    '12',
    'Leicestershire, Lincolnshire, Rutland, Northamptonshire',
    now()),
    (
    '13',
    'Norfolk, Essex, Suffolk',
    now()),
    (
    '14',
    'North and West Yorkshire',
    now()),
    (
    '15',
    'Staffordshire and West Mercia',
    now()),
    (
    '16',
    'Surrey and Sussex',
    now()),
    (
    '17',
    'Thames Valley',
    now()),
    (
    '18',
    'West Midlands and Warwickshire',
    now())
;
