package uk.gov.hmcts.reform.locationrefdata.camel.constants;

public final class LrdDataLoadConstants {

    private LrdDataLoadConstants() {

    }

    public static final String ALPHANUMERIC_UNDERSCORE_REGEX = "[0-9a-zA-Z_]+";
    public static final String YES_NO_BLANK_REGEX = "[YN]?";
    public static final String SERVICE_ID_REGEX = "([A-Z]{3}[0-9])?";
    public static final String INVALID_EPIMS_ID = "Epims id is invalid - can contain only alphanumeric characters";
    public static final String INVALID_SERVICE_ID =
        "ServiceID is invalid - should be 3 uppercase letters followed by a digit";
    public static final String IS_READY_TO_AUDIT = "IS_READY_TO_AUDIT";
    public static final String REGION_ID = "region_id";
    public static final String REGION_ID_NOT_EXISTS = "region_id does not exist";
    public static final String CLUSTER_ID = "cluster_id";
    public static final String CLUSTER_ID_NOT_EXISTS = "cluster_id does not exist";
    public static final String EPIMMS_ID = "epimms_id";
    public static final String EPIMMS_ID_NOT_EXISTS = "epimms_id does not exist";
    public static final String COURT_TYPE_ID = "court_type_id";
    public static final String COURT_TYPE_ID_NOT_EXISTS = "court_type_id does not exist";
    public static final String SERVICE_CODE = "service_code";
    public static final String SERVICE_CODE_NOT_EXISTS = "service_code does not exist";
    public static final String SERVICE_CODE_BLANK = "service_code is blank - row not loaded";
    public static final String PARENT_LOCATION = "parent_location";
    public static final String PARENT_LOCATION_NOT_EXISTS =
        "parent_location does not exist for the service_code";
    public static final String DISTRICT_REGISTRY_VENUE_ID = "district_registry_venue_id";
    public static final String DISTRICT_REGISTRY_VENUE_ID_NOT_EXISTS =
        "district_registry_venue_id does not exist for the service_code";
    public static final String APPEAL_CENTRE_VENUE_ID = "appeal_centre_venue_id";
    public static final String APPEAL_CENTRE_VENUE_ID_NOT_EXISTS =
        "appeal_centre_venue_id does not exist for the service_code";
    public static final String IS_CASE_MANAGEMENT_LOCATION = "is_case_management_location";
    public static final String IS_HEARING_LOCATION = "is_hearing_location";
    public static final String IS_TEMPORARY_LOCATION = "is_temporary_location";
    public static final String IS_NIGHTINGALE_COURT = "is_nightingale_court";
    public static final String IS_DISTRICT_REGISTRY = "is_district_registry";
    public static final String IS_APPEAL_CENTRE = "is_appeal_centre";
    public static final String INVALID_FLAG = "should be Y or N";
    public static final String DATE_PATTERN = "\\d{2}-\\d{2}-\\d{4}\\s\\d{2}:\\d{2}:\\d{2}";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

}
