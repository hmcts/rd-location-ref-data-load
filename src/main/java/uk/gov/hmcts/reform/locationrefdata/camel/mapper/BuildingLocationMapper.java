package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trimNumeric;

@Component
public class BuildingLocationMapper implements IMapper {

    @Override
    public Map<String, Object> getMap(Object buildingLocationObj) {
        BuildingLocation buildingLocation = (BuildingLocation) buildingLocationObj;
        Map<String, Object> buildingLocationParamMap = new HashMap<>();
        buildingLocationParamMap.put("epimms_id", trim(buildingLocation.getEpimmsId()));
        buildingLocationParamMap.put("building_location_name", trim(buildingLocation.getBuildingLocationName()));
        buildingLocationParamMap.put("building_location_status", trim(buildingLocation.getBuildingLocationStatus()));
        buildingLocationParamMap.put("area", trim(buildingLocation.getArea()));
        buildingLocationParamMap.put("region_id", trimNumeric(buildingLocation.getRegionId()));
        buildingLocationParamMap.put("cluster_id", trimNumeric(buildingLocation.getClusterId()));
        buildingLocationParamMap.put("court_finder_url", trim(buildingLocation.getCourtFinderUrl()));
        buildingLocationParamMap.put("postcode", trim(buildingLocation.getPostcode()));
        buildingLocationParamMap.put("address", trim(buildingLocation.getAddress()));
        buildingLocationParamMap.put("welsh_building_location_name",
                                     trim(buildingLocation.getWelshBuildingLocationName()));
        buildingLocationParamMap.put("welsh_address",
                                     trim(buildingLocation.getWelshAddress()));
        buildingLocationParamMap.put("uprn",
                                     trim(buildingLocation.getUprn()));
        buildingLocationParamMap.put("latitude",
                                     buildingLocation.getLatitude());
        buildingLocationParamMap.put("longitude",
                                     buildingLocation.getLongitude());
        buildingLocationParamMap.put("mrd_building_location_id",
                                     trimNumeric(buildingLocation.getMrdBuildingLocationId()));
        buildingLocationParamMap.put("mrd_created_time",buildingLocation.getMrdCreatedTime());
        buildingLocationParamMap.put("mrd_updated_time",buildingLocation.getMrdUpdatedTime());
        buildingLocationParamMap.put("mrd_deleted_time",buildingLocation.getMrdDeletedTime());
        return buildingLocationParamMap;
    }

}
