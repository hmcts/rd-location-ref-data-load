package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuildingLocationMapperTest {

    @Spy
    BuildingLocationMapper mapper = new BuildingLocationMapper();

    @Test
    void testMapper() {
        BuildingLocation location = BuildingLocation.builder()
            .buildingLocationName("building 1")
            .postcode("E1 23A")
            .address("Address ABC")
            .area("Area ABCD")
            .clusterId("123")
            .courtFinderUrl("website url 1")
            .regionId("123")
            .epimmsId("epims1")
            .buildingLocationStatus("OPEN")
            .welshBuildingLocationName("welsh building")
            .build();

        var expected = new HashMap<String, Object>();
        expected.put("building_location_name", "building 1");
        expected.put("postcode", "E1 23A");
        expected.put("address", "Address ABC");
        expected.put("area", "Area ABCD");
        expected.put("cluster_id", "123");
        expected.put("court_finder_url", "website url 1");
        expected.put("region_id", "123");
        expected.put("epimms_id", "epims1");
        expected.put("building_location_status", "OPEN");
        expected.put("welsh_building_location_name", "welsh building");

        Map<String, Object> actual = mapper.getMap(location);

        verify(mapper, times(1)).getMap(location);
        assertThat(actual).hasSize(10).isEqualTo(expected);
    }

}
