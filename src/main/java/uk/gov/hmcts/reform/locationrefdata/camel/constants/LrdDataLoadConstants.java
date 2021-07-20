package uk.gov.hmcts.reform.locationrefdata.camel.constants;

public final class LrdDataLoadConstants {

    private LrdDataLoadConstants() {

    }

    public static final String ALPHANUMERIC_UNDERSCORE_REGEX = "[0-9a-zA-Z_]+";
    public static final String INVALID_EPIMS_ID = "Epims id is invalid - can contain only alphanumeric characters";
    public static final String IS_READY_TO_AUDIT = "IS_READY_TO_AUDIT";
    public static final String REGION_ID = "region_id";
    public static final String REGION_ID_NOT_EXISTS = "region_id does not exist";
    public static final String CLUSTER_ID = "cluster_id";
    public static final String CLUSTER_ID_NOT_EXISTS = "cluster_id does not exist";

}
