package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingLocationTest {


    @Test
    void testBuildingLocationBuilder() {
        BuildingLocation buildingLocation = BuildingLocation.builder()
            .epimmsId("Epimms ID")
            .buildingLocationName("Building Location Name")
            .buildingLocationStatus("Building Location Status")
            .address("Address")
            .area("Area")
            .clusterId("1")
            .courtFinderUrl("Court Finder URL")
            .regionId("2")
            .postcode("Postcode")
            .build();

        assertEquals("Epimms ID", buildingLocation.getEpimmsId());
        assertEquals("Building Location Name", buildingLocation.getBuildingLocationName());
        assertEquals("Building Location Status", buildingLocation.getBuildingLocationStatus());
        assertEquals("Address", buildingLocation.getAddress());
        assertEquals("Area", buildingLocation.getArea());
        assertEquals("1", buildingLocation.getClusterId());
        assertEquals("Court Finder URL", buildingLocation.getCourtFinderUrl());
        assertEquals("2", buildingLocation.getRegionId());
        assertEquals("Postcode", buildingLocation.getPostcode());

        String buildingLocationString = BuildingLocation.builder()
            .epimmsId("Epimms ID")
            .buildingLocationName("Building Location Name")
            .buildingLocationStatus("Building Location Status")
            .address("Address")
            .area("Area")
            .clusterId("1")
            .courtFinderUrl("Court Finder URL")
            .regionId("2")
            .postcode("Postode")
            .toString();

        assertEquals("BuildingLocation.BuildingLocationBuilder(epimmsId=Epimms ID, "
                         + "buildingLocationName=Building Location Name, "
                         + "buildingLocationStatus=Building Location Status, area=Area, regionId=2, clusterId=1, "
                         + "courtFinderUrl=Court Finder URL, postcode=Postode, address=Address)",
                     buildingLocationString);
    }


}
