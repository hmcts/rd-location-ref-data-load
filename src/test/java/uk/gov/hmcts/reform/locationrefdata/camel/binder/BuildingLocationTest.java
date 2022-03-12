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
            .welshBuildingLocationName("Welsh Building")
            .welshAddress("Welsh Address")
            .uprn("1234")
            .latitude(1111.2222)
            .longitude(3333.4444)
            .mrdBuildingLocationId("98765")
            .mrdCreatedTime("2020-01-01 00:00:00")
            .mrdUpdatedTime("2030-01-01 00:00:00")
            .mrdDeletedTime("2040-01-01 00:00:00")
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
        assertEquals("Welsh Building", buildingLocation.getWelshBuildingLocationName());
        assertEquals("1234", buildingLocation.getUprn());
        assertEquals(1111.2222, buildingLocation.getLatitude());
        assertEquals(3333.4444, buildingLocation.getLongitude());
        assertEquals("98765", buildingLocation.getMrdBuildingLocationId());
        assertEquals("2020-01-01 00:00:00", buildingLocation.getMrdCreatedTime());
        assertEquals("2030-01-01 00:00:00", buildingLocation.getMrdUpdatedTime());
        assertEquals("2040-01-01 00:00:00", buildingLocation.getMrdDeletedTime());


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
            .welshBuildingLocationName("Welsh Building")
            .welshAddress("Welsh Address")
            .uprn("1234")
            .latitude(111.222)
            .longitude(333.444)
            .mrdBuildingLocationId("24679")
            .toString();

        assertEquals("BuildingLocation.BuildingLocationBuilder(epimmsId=Epimms ID, "
                         + "buildingLocationName=Building Location Name, "
                         + "buildingLocationStatus=Building Location Status, area=Area, regionId=2, clusterId=1, "
                         + "courtFinderUrl=Court Finder URL, postcode=Postode, address=Address, "
                         + "welshBuildingLocationName=Welsh Building, welshAddress=Welsh Address, "
                         + "uprn=1234, latitude=111.222, longitude=333.444, mrdBuildingLocationId=24679, "
                         + "mrdCreatedTime=null, mrdUpdatedTime=null, mrdDeletedTime=null)",
                     buildingLocationString);
    }


}
