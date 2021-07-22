package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CourtVenueTest {

    @Test
    public void testCourtVenueBuilder() {

        CourtVenue courtVenue = CourtVenue.builder()
            .courtName("courtName")
            .courtAddress("courtName")
            .welshCourtAddress("welshCourtAddress")
            .courtStatus("courtStatus")
            .courtTypeId("1")
            .dxAddress("dxAddress")
            .courtLocationCode("courtLocationCode")
            .courtOpenDate("courtOpenDate")
            .closedDate("closedDate")
            .clusterId("2")
            .epimmsId("epimmsId")
            .openForPublic("openForPublic")
            .phoneNumber("phoneNumber")
            .postcode("postcode")
            .welshSiteName("welshSiteName")
            .siteName("siteName")
            .regionId("3")
            .build();

        assertThat(courtVenue.getCourtName()).isEqualTo("courtName");
        assertThat(courtVenue.getCourtAddress()).isEqualTo("courtName");
        assertThat(courtVenue.getWelshCourtAddress()).isEqualTo("welshCourtAddress");
        assertThat(courtVenue.getCourtStatus()).isEqualTo("courtStatus");
        assertThat(courtVenue.getCourtTypeId()).isEqualTo("1");
        assertThat(courtVenue.getDxAddress()).isEqualTo("dxAddress");
        assertThat(courtVenue.getCourtLocationCode()).isEqualTo("courtLocationCode");
        assertThat(courtVenue.getCourtOpenDate()).isEqualTo("courtOpenDate");
        assertThat(courtVenue.getClosedDate()).isEqualTo("closedDate");
        assertThat(courtVenue.getClusterId()).isEqualTo("2");
        assertThat(courtVenue.getEpimmsId()).isEqualTo("epimmsId");
        assertThat(courtVenue.getOpenForPublic()).isEqualTo("openForPublic");
        assertThat(courtVenue.getPhoneNumber()).isEqualTo("phoneNumber");
        assertThat(courtVenue.getPostcode()).isEqualTo("postcode");
        assertThat(courtVenue.getWelshSiteName()).isEqualTo("welshSiteName");
        assertThat(courtVenue.getSiteName()).isEqualTo("siteName");
        assertThat(courtVenue.getRegionId()).isEqualTo("3");

        String courtVenueString = CourtVenue.builder()
            .courtName("courtName")
            .courtAddress("courtName")
            .welshCourtAddress("welshCourtAddress")
            .courtStatus("courtStatus")
            .courtTypeId("1")
            .dxAddress("dxAddress")
            .courtLocationCode("courtLocationCode")
            .courtOpenDate("courtOpenDate")
            .closedDate("closedDate")
            .clusterId("2")
            .epimmsId("epimmsId")
            .openForPublic("openForPublic")
            .phoneNumber("phoneNumber")
            .postcode("postcode")
            .welshSiteName("welshSiteName")
            .siteName("siteName")
            .regionId("3")
            .toString();

        assertThat(courtVenueString)
            .isEqualTo("CourtVenue.CourtVenueBuilder(epimmsId=epimmsId, siteName=siteName, "
                           + "courtName=courtName, courtStatus=courtStatus, "
                           + "courtOpenDate=courtOpenDate, regionId=3, courtTypeId=1, "
                           + "clusterId=2, openForPublic=openForPublic, courtAddress=courtName, "
                           + "postcode=postcode, phoneNumber=phoneNumber, closedDate=closedDate, "
                           + "courtLocationCode=courtLocationCode, dxAddress=dxAddress, "
                           + "welshSiteName=welshSiteName, welshCourtAddress=welshCourtAddress)");
    }
}
