package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trimNumeric;

@Component
public class BuildingLocationMapper implements IMapper {

    @Override
    public Map<String, Object> getMap(Object buildingLocationObj) {
        BuildingLocation buildingLocation = (BuildingLocation) buildingLocationObj;

        Map<String, Object> map = CommonMapper.getMap(buildingLocation.getEpimmsId(),
                                                       buildingLocation.getRegionId(),
                                                       buildingLocation.getClusterId(),
                                                       buildingLocation.getPostcode(),
                                                       buildingLocation.getUprn(),
                                                       buildingLocation.getMrdBuildingLocationId(),
                                                       buildingLocation.getMrdCreatedTime(),
                                                       buildingLocation.getMrdUpdatedTime(),
                                                       buildingLocation.getMrdDeletedTime());

        map.put("building_location_name", trim(buildingLocation.getBuildingLocationName()));
        map.put("building_location_status", trim(buildingLocation.getBuildingLocationStatus()));
        map.put("area", trim(buildingLocation.getArea()));
        map.put("court_finder_url", trim(buildingLocation.getCourtFinderUrl()));
        map.put("address", trim(buildingLocation.getAddress()));
        map.put("welsh_building_location_name",
                                     trim(buildingLocation.getWelshBuildingLocationName()));
        map.put("welsh_address", trim(buildingLocation.getWelshAddress()));
        map.put("latitude", buildingLocation.getLatitude());
        map.put("longitude", buildingLocation.getLongitude());

        return map;
    }

}
