CREATE TABLE court_venue_location_details (
    court_venue_id BIGINT NOT NULL,
    venue_name VARCHAR(256),
    is_case_management_location VARCHAR(1),
    is_hearing_location VARCHAR(1),
    welsh_venue_name VARCHAR(256),
    is_temporary_location VARCHAR(1),
    is_nightingale_court VARCHAR(1),
    location_type VARCHAR(16),
    parent_location VARCHAR(16),
    created_time TIMESTAMP,
    updated_time TIMESTAMP,
    CONSTRAINT court_venue_location_details_pk PRIMARY KEY (court_venue_id),
    CONSTRAINT court_venue_location_details_fk FOREIGN KEY (court_venue_id)
        REFERENCES court_venue (court_venue_id) ON DELETE CASCADE
);

CREATE TABLE court_venue_reference_details (
    court_venue_id BIGINT NOT NULL,
    welsh_court_name VARCHAR(256),
    uprn VARCHAR(16),
    venue_ou_code VARCHAR(16),
    mrd_building_location_id VARCHAR(16),
    mrd_venue_id VARCHAR(16),
    service_url VARCHAR(1024),
    fact_url VARCHAR(1024),
    external_short_name VARCHAR(80),
    welsh_external_short_name VARCHAR(80),
    created_time TIMESTAMP,
    updated_time TIMESTAMP,
    CONSTRAINT court_venue_reference_details_pk PRIMARY KEY (court_venue_id),
    CONSTRAINT court_venue_reference_details_fk FOREIGN KEY (court_venue_id)
        REFERENCES court_venue (court_venue_id) ON DELETE CASCADE
);
