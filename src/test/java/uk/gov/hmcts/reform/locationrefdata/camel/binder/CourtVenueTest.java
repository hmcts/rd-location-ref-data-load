package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtVenueTest {

    @Test
    void testCourtVenueBuilder() {

        CourtVenue courtVenue = CourtVenue.builder()
            .courtName("courtName")
            .courtAddress("courtAddress")
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
            .venueName("venueName")
            .isCaseManagementLocation("Y")
            .isHearingLocation("N")
            .build();

        assertEquals("courtName", courtVenue.getCourtName());
        assertEquals("courtAddress", courtVenue.getCourtAddress());
        assertEquals("welshCourtAddress", courtVenue.getWelshCourtAddress());
        assertEquals("courtStatus", courtVenue.getCourtStatus());
        assertEquals("1", courtVenue.getCourtTypeId());
        assertEquals("dxAddress", courtVenue.getDxAddress());
        assertEquals("courtLocationCode", courtVenue.getCourtLocationCode());
        assertEquals("courtOpenDate", courtVenue.getCourtOpenDate());
        assertEquals("closedDate", courtVenue.getClosedDate());
        assertEquals("2", courtVenue.getClusterId());
        assertEquals("epimmsId", courtVenue.getEpimmsId());
        assertEquals("openForPublic", courtVenue.getOpenForPublic());
        assertEquals("phoneNumber", courtVenue.getPhoneNumber());
        assertEquals("postcode", courtVenue.getPostcode());
        assertEquals("welshSiteName", courtVenue.getWelshSiteName());
        assertEquals("siteName", courtVenue.getSiteName());
        assertEquals("3", courtVenue.getRegionId());
        assertEquals("venueName", courtVenue.getVenueName());
        assertEquals("Y", courtVenue.getIsCaseManagementLocation());
        assertEquals("N", courtVenue.getIsHearingLocation());
    }


    @Test
    void testCourtVenueBuilderNewFields() {

        CourtVenue courtVenue = CourtVenue.builder()
            .welshVenueName("testVenue")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType("Court")
            .parentLocation("366559")
            .build();

        assertEquals("testVenue", courtVenue.getWelshVenueName());
        assertEquals("N", courtVenue.getIsTemporaryLocation());
        assertEquals("N", courtVenue.getIsNightingaleCourt());
        assertEquals("Court", courtVenue.getLocationType());
        assertEquals("366559", courtVenue.getParentLocation());

    }


    @Test
    void testCourtVenueBuilderCheck() {

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
            .venueName("venueName")
            .isCaseManagementLocation("Y")
            .isHearingLocation("N")
            .welshVenueName("testVenue")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType("Court")
            .parentLocation("366559")
            .toString();

        assertEquals("CourtVenue.CourtVenueBuilder(epimmsId=epimmsId, siteName=siteName, "
                         + "courtName=courtName, courtStatus=courtStatus, "
                         + "courtOpenDate=courtOpenDate, regionId=3, courtTypeId=1, "
                         + "clusterId=2, openForPublic=openForPublic, courtAddress=courtName, "
                         + "postcode=postcode, phoneNumber=phoneNumber, closedDate=closedDate, "
                         + "courtLocationCode=courtLocationCode, dxAddress=dxAddress, "
                         + "welshSiteName=welshSiteName, welshCourtAddress=welshCourtAddress, "
                         + "venueName=venueName, isCaseManagementLocation=Y, isHearingLocation=N, "
                         + "welshVenueName=testVenue, isTemporaryLocation=N, isNightingaleCourt=N, "
                         + "locationType=Court, parentLocation=366559)",
                     courtVenueString);

    }
}
