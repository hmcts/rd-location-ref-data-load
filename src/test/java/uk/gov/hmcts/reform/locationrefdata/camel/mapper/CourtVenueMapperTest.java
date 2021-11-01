package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.HashMap;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CourtVenueMapperTest {

    CourtVenueMapper courtVenueMapper = spy(new CourtVenueMapper());

    @Test
    void testGetMap() {
        CourtVenue courtVenue = CourtVenue.builder()
            .epimmsId("123456")
            .siteName("Test Site")
            .courtName("Test Court Name")
            .courtStatus("Open")
            .courtOpenDate("12/12/12")
            .regionId("1")
            .courtTypeId("2")
            .clusterId("3")
            .openForPublic("Yes")
            .courtAddress("Test Court Address")
            .postcode("ABC 123")
            .phoneNumber("12343434")
            .closedDate("12/03/21")
            .courtLocationCode("12AB")
            .dxAddress("Test Dx Address")
            .welshSiteName("Test Welsh Site Name")
            .welshCourtAddress("Test Welsh Court Address")
            .venueName("Test Venue Name")
            .isCaseManagementLocation("Y")
            .isHearingLocation("N")
            .build();

        var expectedMap = new HashMap<String, Object>();
        expectedMap.put("epimms_id", "123456");
        expectedMap.put("site_name", "Test Site");
        expectedMap.put("court_name", "Test Court Name");
        expectedMap.put("court_status", "Open");
        expectedMap.put("court_open_date", "12/12/12");
        expectedMap.put("region_id", "1");
        expectedMap.put("court_type_id", "2");
        expectedMap.put("cluster_id", "3");
        expectedMap.put("open_for_public", "Yes");
        expectedMap.put("court_address", "Test Court Address");
        expectedMap.put("postcode", "ABC 123");
        expectedMap.put("phone_number", "12343434");
        expectedMap.put("closed_date", "12/03/21");
        expectedMap.put("court_location_code", "12AB");
        expectedMap.put("dx_address", "Test Dx Address");
        expectedMap.put("welsh_site_name", "Test Welsh Site Name");
        expectedMap.put("welsh_court_address", "Test Welsh Court Address");
        expectedMap.put("venue_name", "Test Venue Name");
        expectedMap.put("is_case_management_location", "Y");
        expectedMap.put("is_hearing_location", "N");

        var actualMap = courtVenueMapper.getMap(courtVenue);

        Assertions.assertEquals(actualMap, expectedMap);

        verify(courtVenueMapper, times(1)).getMap(courtVenue);
    }

}
