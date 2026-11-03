package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            .welshExternalShortName("Welsh External Court")
            .serviceCode("serviceCode1")
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
        assertEquals("serviceCode1", courtVenue.getServiceCode());
    }


    @Test
    void testCourtVenueBuilderNewFields() {

        CourtVenue courtVenue = CourtVenue.builder()
            .welshVenueName("testVenue")
            .isTemporaryLocation("N")
            .isNightingaleCourt("N")
            .locationType("Court")
            .parentLocation("366559")
            .welshCourtName("testWelshCourtName")
            .uprn("uprn123")
            .venueOuCode("venueOuCode1")
            .mrdBuildingLocationId("mrdBuildingLocationId1")
            .mrdVenueId("MRD-AAA-1")
            .serviceUrl("serviceUrl1")
            .factUrl("factUrl1")
            .mrdCreatedTime("2020-01-01 00:00:00")
            .mrdUpdatedTime("2030-01-01 00:00:00")
            .mrdDeletedTime("2040-01-01 00:00:00")
            .externalShortName("External Court")
            .welshExternalShortName("Welsh External Court")
            .serviceCode("serviceCode1")
            .districtRegistrySiteName("districtRegistrySiteName")
            .districtRegistryWelshSiteName("districtRegistryWelshSiteName")
            .districtRegistryExternalShortName("districtRegistryExternalShortName")
            .districtRegistryWelshExternalShortName("districtRegistryWelshExternalShortName")
            .contactEmail("contact@example.com")
            .breathingSpaceEmail("breathing@example.com")
            .isDistrictRegistry("Y")
            .isAppealCentre("N")
            .districtRegistryVenueId("districtRegistryVenueId")
            .appealCentreVenueId("appealCentreVenueId")
            .build();

        assertEquals("testVenue", courtVenue.getWelshVenueName());
        assertEquals("N", courtVenue.getIsTemporaryLocation());
        assertEquals("N", courtVenue.getIsNightingaleCourt());
        assertEquals("Court", courtVenue.getLocationType());
        assertEquals("366559", courtVenue.getParentLocation());
        assertEquals("testWelshCourtName", courtVenue.getWelshCourtName());
        assertEquals("uprn123", courtVenue.getUprn());
        assertEquals("venueOuCode1", courtVenue.getVenueOuCode());
        assertEquals("mrdBuildingLocationId1", courtVenue.getMrdBuildingLocationId());
        assertEquals("MRD-AAA-1", courtVenue.getMrdVenueId());
        assertEquals("serviceUrl1", courtVenue.getServiceUrl());
        assertEquals("factUrl1", courtVenue.getFactUrl());
        assertEquals("2020-01-01 00:00:00", courtVenue.getMrdCreatedTime());
        assertEquals("2030-01-01 00:00:00", courtVenue.getMrdUpdatedTime());
        assertEquals("2040-01-01 00:00:00", courtVenue.getMrdDeletedTime());
        assertEquals("External Court", courtVenue.getExternalShortName());
        assertEquals("Welsh External Court", courtVenue.getWelshExternalShortName());
        assertEquals("serviceCode1", courtVenue.getServiceCode());
        assertEquals("districtRegistrySiteName", courtVenue.getDistrictRegistrySiteName());
        assertEquals("districtRegistryWelshSiteName", courtVenue.getDistrictRegistryWelshSiteName());
        assertEquals("districtRegistryExternalShortName", courtVenue.getDistrictRegistryExternalShortName());
        assertEquals("districtRegistryWelshExternalShortName", courtVenue.getDistrictRegistryWelshExternalShortName());
        assertEquals("contact@example.com", courtVenue.getContactEmail());
        assertEquals("breathing@example.com", courtVenue.getBreathingSpaceEmail());
        assertEquals("Y", courtVenue.getIsDistrictRegistry());
        assertEquals("N", courtVenue.getIsAppealCentre());
        assertEquals("districtRegistryVenueId", courtVenue.getDistrictRegistryVenueId());
        assertEquals("appealCentreVenueId", courtVenue.getAppealCentreVenueId());
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
            .welshCourtName("testWelshCourtName")
            .uprn("uprn123")
            .venueOuCode("venueOuCode1")
            .mrdBuildingLocationId("mrdBuildingLocationId1")
            .mrdVenueId("MRD-AAA-1")
            .serviceUrl("serviceUrl1")
            .factUrl("factUrl1")
            .externalShortName("shortName")
            .welshExternalShortName("welshExternalShortName")
            .serviceCode("serviceCode1")
            .districtRegistrySiteName("districtRegistrySiteName")
            .districtRegistryWelshSiteName("districtRegistryWelshSiteName")
            .districtRegistryExternalShortName("districtRegistryExternalShortName")
            .districtRegistryWelshExternalShortName("districtRegistryWelshExternalShortName")
            .contactEmail("contact@example.com")
            .breathingSpaceEmail("breathing@example.com")
            .isDistrictRegistry("Y")
            .isAppealCentre("N")
            .districtRegistryVenueId("districtRegistryVenueId")
            .appealCentreVenueId("appealCentreVenueId")
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
                         + "locationType=Court, parentLocation=366559, welshCourtName=testWelshCourtName, "
                         + "uprn=uprn123, venueOuCode=venueOuCode1, mrdBuildingLocationId=mrdBuildingLocationId1, "
                         + "mrdVenueId=MRD-AAA-1, serviceUrl=serviceUrl1, factUrl=factUrl1, "
                         + "mrdCreatedTime=null, mrdUpdatedTime=null, mrdDeletedTime=null, "
                         + "externalShortName=shortName, "
                         + "welshExternalShortName=welshExternalShortName, "
                         + "serviceCode=serviceCode1, "
                         + "contactEmail=contact@example.com, breathingSpaceEmail=breathing@example.com, "
                         + "isDistrictRegistry=Y, isAppealCentre=N, "
                         + "districtRegistryVenueId=districtRegistryVenueId, "
                         + "appealCentreVenueId=appealCentreVenueId, "
                         + "districtRegistrySiteName=districtRegistrySiteName, "
                         + "districtRegistryWelshSiteName=districtRegistryWelshSiteName, "
                         + "districtRegistryExternalShortName=districtRegistryExternalShortName, "
                         + "districtRegistryWelshExternalShortName=districtRegistryWelshExternalShortName)",
                     courtVenueString);

    }

    @Test
    void testFlagFieldsOnlyAcceptYesNoAndBlank() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        List<BiConsumer<CourtVenue, String>> flagSetters = List.of(
            CourtVenue::setIsCaseManagementLocation,
            CourtVenue::setIsHearingLocation,
            CourtVenue::setIsTemporaryLocation,
            CourtVenue::setIsNightingaleCourt,
            CourtVenue::setIsDistrictRegistry,
            CourtVenue::setIsAppealCentre
        );

        for (BiConsumer<CourtVenue, String> setter : flagSetters) {
            assertTrue(validator.validate(withFlag(setter, "Y")).isEmpty());
            assertTrue(validator.validate(withFlag(setter, "N")).isEmpty());
            assertTrue(validator.validate(withFlag(setter, "")).isEmpty());
            assertFalse(validator.validate(withFlag(setter, "Yes")).isEmpty());
            assertFalse(validator.validate(withFlag(setter, "y")).isEmpty());
            assertFalse(validator.validate(withFlag(setter, " ")).isEmpty());
        }
    }

    @Test
    void testServiceCodeFormat() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertTrue(validator.validate(withServiceCode("AAA1")).isEmpty());
        assertTrue(validator.validate(withServiceCode("")).isEmpty());
        assertFalse(validator.validate(withServiceCode("AA1")).isEmpty());
        assertFalse(validator.validate(withServiceCode("AAAA1")).isEmpty());
        assertFalse(validator.validate(withServiceCode("AAA12")).isEmpty());
        assertFalse(validator.validate(withServiceCode("AAA")).isEmpty());
        assertFalse(validator.validate(withServiceCode("aaa1")).isEmpty());
        assertFalse(validator.validate(withServiceCode("AAAa")).isEmpty());
    }

    @Test
    void testMrdVenueIdFormat() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertTrue(validator.validate(withMrdVenueId("MRD-AAA-1")).isEmpty());
        assertTrue(validator.validate(withMrdVenueId("MRD-ZZZ-123456")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("MRD-AA-1")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("MRD-AAAA-1")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("mrd-AAA-1")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("MRD-AAA-")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("MRD-AAA-1A")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("AAA-1")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId("")).isEmpty());
        assertFalse(validator.validate(withMrdVenueId(null)).isEmpty());
    }

    @Test
    void testEmailFieldsOnlyAcceptValidEmailOrBlank() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        List<BiConsumer<CourtVenue, String>> emailSetters = List.of(
            CourtVenue::setContactEmail,
            CourtVenue::setBreathingSpaceEmail
        );

        for (BiConsumer<CourtVenue, String> setter : emailSetters) {
            assertTrue(validator.validate(withEmail(setter, "contact@example.com")).isEmpty());
            assertTrue(validator.validate(withEmail(setter, "first.last+tag@example.co.uk")).isEmpty());
            assertTrue(validator.validate(withEmail(setter, "")).isEmpty());
            assertTrue(validator.validate(withEmail(setter, null)).isEmpty());
            assertFalse(validator.validate(withEmail(setter, "not-an-email")).isEmpty());
            assertFalse(validator.validate(withEmail(setter, "user@localhost")).isEmpty());
            assertFalse(validator.validate(withEmail(setter, "user@exa_mple.com")).isEmpty());
        }
    }

    private CourtVenue withEmail(BiConsumer<CourtVenue, String> setter, String value) {
        CourtVenue courtVenue = CourtVenue.builder()
            .epimmsId("123456")
            .siteName("siteName")
            .openForPublic("Yes")
            .mrdVenueId("MRD-AAA-9")
            .build();
        setter.accept(courtVenue, value);
        return courtVenue;
    }

    private CourtVenue withServiceCode(String serviceCode) {
        return CourtVenue.builder()
            .epimmsId("123456")
            .siteName("siteName")
            .openForPublic("Yes")
            .mrdVenueId("MRD-AAA-9")
            .serviceCode(serviceCode)
            .build();
    }

    private CourtVenue withMrdVenueId(String mrdVenueId) {
        return CourtVenue.builder()
            .epimmsId("123456")
            .siteName("siteName")
            .openForPublic("Yes")
            .mrdVenueId(mrdVenueId)
            .build();
    }

    private CourtVenue withFlag(BiConsumer<CourtVenue, String> setter, String value) {
        CourtVenue courtVenue = CourtVenue.builder()
            .epimmsId("123456")
            .siteName("siteName")
            .openForPublic("Yes")
            .mrdVenueId("MRD-AAA-9")
            .build();
        setter.accept(courtVenue, value);
        return courtVenue;
    }
}
