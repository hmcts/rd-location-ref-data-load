package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildingLocationTest {


    @Test
    public void testBuildingLocationBuilder() {
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

        assertThat(buildingLocation.getEpimmsId()).isEqualTo("Epimms ID");
        assertThat(buildingLocation.getBuildingLocationName()).isEqualTo("Building Location Name");
        assertThat(buildingLocation.getBuildingLocationStatus()).isEqualTo("Building Location Status");
        assertThat(buildingLocation.getAddress()).isEqualTo("Address");
        assertThat(buildingLocation.getArea()).isEqualTo("Area");
        assertThat(buildingLocation.getClusterId()).isEqualTo("1");
        assertThat(buildingLocation.getCourtFinderUrl()).isEqualTo("Court Finder URL");
        assertThat(buildingLocation.getRegionId()).isEqualTo("2");
        assertThat(buildingLocation.getPostcode()).isEqualTo("Postcode");

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

        assertThat(buildingLocationString)
            .isEqualTo("BuildingLocation.BuildingLocationBuilder(epimmsId=Epimms ID, "
                           + "buildingLocationName=Building Location Name, "
                           + "buildingLocationStatus=Building Location Status, area=Area, regionId=2, clusterId=1, "
                           + "courtFinderUrl=Court Finder URL, postcode=Postode, address=Address)");
    }


}
