create table region (
	region_id varchar(16),
	description varchar(256),
	created_time timestamp,
	updated_time timestamp,
	welsh_description varchar(256),
	CONSTRAINT region_id_pk PRIMARY KEY (region_id)
);

create table building_location(
	building_location_id varchar(16),
	epimms_id varchar(16) NOT NULL,
	building_location_name varchar(256) NOT NULL,
	created_time timestamp,
	updated_time timestamp,
	building_location_status_id varchar(16),
	area varchar(16),
	region_id varchar(16),
	cluster_id varchar(16),
	court_finder_url varchar(512),
	postcode varchar(8) NOT NULL,
	address varchar(512) NOT NULL,
	constraint building_location_pk primary key (building_location_id),
	constraint epimms_id_uq unique (epimms_id)
);

create table district_family_jurisdiction (
	district_family_jurisdiction_id varchar(16) NOT NULL,
	description varchar(256),
	created_time timestamp,
	updated_time timestamp,
	welsh_description varchar(256),
	CONSTRAINT district_family_jurisdiction_id_pk PRIMARY KEY (district_family_jurisdiction_id)
);

create table court_district_family_jurisdiction_assoc(
  court_district_family_jurisdiction_assoc_id varchar(16) NOT NULL,
  district_family_jurisdiction_id varchar(16),
	court_location_id varchar(16),
	created_time timestamp,
	updated_time timestamp,
	CONSTRAINT court_district_family_jurisdiction_assoc_id_pk PRIMARY KEY (court_district_family_jurisdiction_assoc_id));

create table district_civil_jurisdiction (
	district_civil_jurisdiction_id varchar(16) NOT NULL,
	description varchar(256),
	created_time timestamp,
	updated_time timestamp,
	welsh_description varchar(256),
	CONSTRAINT district_civil_jurisdiction_id_pk PRIMARY KEY (district_civil_jurisdiction_id)

);

create table court_district_civil_jurisdiction_assoc(
  court_district_civil_jurisdiction_assoc_id varchar(16) NOT NULL,
  district_civil_jurisdiction_id varchar(16),
	court_location_id varchar(16),
	created_time timestamp,
	updated_time timestamp,
	CONSTRAINT court_district_civil_jurisdiction_assoc_id_pk PRIMARY KEY (court_district_civil_jurisdiction_assoc_id));

create table cluster (
	cluster_id varchar(16) NOT NULL,
	cluster_name varchar(256),
	welsh_cluster_name varchar(256),
	created_time timestamp,
	updated_time timestamp,
	CONSTRAINT cluster_id_pk PRIMARY KEY (cluster_id)
);

create table court_location_category(
  court_location_category_id varchar(16) NOT NULL,
	court_location_category varchar(128) NOT NULL,
	welsh_court_location_category varchar(128) NOT NULL,
	created_time timestamp,
	updated_time timestamp,
	CONSTRAINT court_location_category_id_pk PRIMARY KEY (court_location_category_id)) ;

create table court_location_category_service_assoc(
  court_location_category_service_assoc_id varchar(16) NOT NULL,
  service_code varchar(16),
	court_location_category_id varchar(16),
	created_time timestamp,
	updated_time timestamp,
	CONSTRAINT court_location_category_service_assoc_id_pk PRIMARY KEY (court_location_category_service_assoc_id)) ;


create table building_location_status(
	building_location_status_id varchar(16),
	status varchar(32),
	welsh_status varchar(32),
	created_time timestamp,
	updated_time timestamp,
	constraint building_location_status_id_pk primary key (building_location_status_id)
);

create table court_location(
	court_location_id varchar(16),
	epimms_id varchar(16) NOT NULL,
	court_location_name varchar(256) NOT NULL,
	created_time timestamp,
	updated_time timestamp,
	region_id varchar(16),
	court_location_category_id varchar(16),
	cluster_id varchar(16),
	open_for_public boolean NOT NULL,
	court_address varchar(512) NOT NULL,
	postcode varchar(8) NOT NULL,
	phone_number varchar(128),
	closed_date timestamp,
	court_location_code varchar(8),
	dx_address varchar(16),
	welsh_court_location_name varchar(256),
	welsh_court_address varchar(512),
	constraint court_id_pk primary key (court_location_id),
	constraint court_location_code_uq unique (court_location_code)
);

alter table BUILDING_LOCATION add CONSTRAINT building_location_region_id_fk FOREIGN KEY (region_id)
REFERENCES REGION (region_id);

alter table BUILDING_LOCATION add CONSTRAINT building_location_status_id_fk FOREIGN KEY (building_location_status_id)
REFERENCES BUILDING_LOCATION_STATUS (building_location_status_id);

alter table BUILDING_LOCATION add CONSTRAINT building_location_cluster_id_fk FOREIGN KEY (cluster_id)
REFERENCES CLUSTER (cluster_id);

alter table COURT_LOCATION add CONSTRAINT court_location_region_id_fk FOREIGN KEY (region_id)
REFERENCES REGION (region_id);

alter table COURT_LOCATION add CONSTRAINT court_location_epimms_id_fk FOREIGN KEY (epimms_id)
REFERENCES BUILDING_LOCATION (epimms_id);

alter table COURT_LOCATION add CONSTRAINT court_location_cluster_id_fk FOREIGN KEY (cluster_id)
REFERENCES CLUSTER (cluster_id);

alter table COURT_LOCATION add CONSTRAINT court_location_location_category_id_fk
FOREIGN KEY (court_location_category_id)
REFERENCES COURT_LOCATION_CATEGORY (court_location_category_id);

alter table COURT_LOCATION_CATEGORY_SERVICE_ASSOC add CONSTRAINT court_location_category_service_assoc_service_code_fk
FOREIGN KEY (service_code)
REFERENCES SERVICE (service_code);

alter table COURT_LOCATION_CATEGORY_SERVICE_ASSOC add CONSTRAINT court_location_category_service_location_category_id_fk
FOREIGN KEY (court_location_category_id)
REFERENCES COURT_LOCATION_CATEGORY (court_location_category_id);

alter table court_district_family_jurisdiction_assoc add CONSTRAINT dfj_court_location_id_fk
FOREIGN KEY (court_location_id)
REFERENCES COURT_LOCATION (court_location_id);

alter table court_district_family_jurisdiction_assoc add CONSTRAINT dfj_court_district_family_jurisdiction_id_fk
FOREIGN KEY (district_family_jurisdiction_id)
REFERENCES DISTRICT_FAMILY_JURISDICTION (district_family_jurisdiction_id);

alter table court_district_civil_jurisdiction_assoc add CONSTRAINT dcj_court_location_id_fk
FOREIGN KEY (court_location_id)
REFERENCES COURT_LOCATION (court_location_id);

alter table court_district_civil_jurisdiction_assoc add CONSTRAINT dcj_court_district_civil_jurisdiction_id_fk
FOREIGN KEY (district_civil_jurisdiction_id)
REFERENCES DISTRICT_CIVIL_JURISDICTION (district_civil_jurisdiction_id);
