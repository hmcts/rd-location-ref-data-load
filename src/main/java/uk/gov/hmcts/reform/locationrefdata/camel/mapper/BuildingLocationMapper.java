package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;

public class BuildingLocationMapper implements IMapper {

    @Override
    public Map<String, Object> getMap(Object buildingLocationObj) {
        BuildingLocation buildingLocation = (BuildingLocation) buildingLocationObj;
        Map<String, Object> buildingLocationParamMap = new HashMap<>();
        buildingLocationParamMap.put("epimms_id", trim(buildingLocation.getEpimsId()));
        buildingLocationParamMap.put("building_location_name", trim(buildingLocation.getBuildingLocationName()));
        buildingLocationParamMap.put("building_location_status", trim(buildingLocation.getStatus()));
        buildingLocationParamMap.put("area", trim(buildingLocation.getArea()));
        buildingLocationParamMap.put("region_id", trim(buildingLocation.getRegionId()));
        buildingLocationParamMap.put("cluster_id", trim(buildingLocation.getClusterId()));
        buildingLocationParamMap.put("court_finder_url", trim(buildingLocation.getCourtFinderUrl()));
        buildingLocationParamMap.put("postcode", trim(buildingLocation.getPostcode()));
        buildingLocationParamMap.put("address", trim(buildingLocation.getAddress()));
        return buildingLocationParamMap;
    }

}
