package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.trim;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trimNumeric;

public class CommonMapper {

    private CommonMapper() {
        // utility class
    }

    public static Map<String, Object> getMap(String epimmsId, String regionId, String clusterId,
                                             String postcode, String uprn, String locationId, String mrdCreatedTime,
                                             String mrdUpdatedTime, String mrdDeletedTime) {
        Map<String, Object> commonMapping = new HashMap<>();

        commonMapping.put("epimms_id", LrdLoadUtils.trim(epimmsId));
        commonMapping.put("region_id", trimNumeric(regionId));
        commonMapping.put("cluster_id", trimNumeric(clusterId));
        commonMapping.put("postcode", LrdLoadUtils.trim(postcode));
        commonMapping.put("uprn", LrdLoadUtils.trim(uprn));
        commonMapping.put("mrd_building_location_id", trimNumeric(locationId));
        commonMapping.put("mrd_created_time", LrdLoadUtils.getDateTimeStamp(mrdCreatedTime));
        commonMapping.put("mrd_updated_time", LrdLoadUtils.getDateTimeStamp(mrdUpdatedTime));
        commonMapping.put("mrd_deleted_time", LrdLoadUtils.getDateTimeStamp(mrdDeletedTime));

        return commonMapping;
    }
}
