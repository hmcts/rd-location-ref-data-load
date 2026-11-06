package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.domain.CommonCsvField;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.DatePattern;

import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.ALPHANUMERIC_UNDERSCORE_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.DATE_ONLY_FORMAT;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.DATE_ONLY_PATTERN;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.DATE_PATTERN;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.DATE_TIME_FORMAT;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.EMAIL_OR_BLANK_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_EMAIL;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_EPIMS_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_FLAG;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_MRD_VENUE_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_PHONE;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_SERVICE_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_URL;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_APPEAL_CENTRE;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_CASE_MANAGEMENT_LOCATION;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_DISTRICT_REGISTRY;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_HEARING_LOCATION;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_NIGHTINGALE_COURT;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_TEMPORARY_LOCATION;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.MRD_VENUE_ID_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.PHONE_OR_BLANK_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.SERVICE_ID_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.URL_OR_BLANK_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.YES_NO_BLANK_REGEX;


@Component
@Setter
@Getter
@ToString
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true, skipField = true)
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CourtVenue extends CommonCsvField {

    @DataField(pos = 1, columnName = "ePIMS_id")
    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = ALPHANUMERIC_UNDERSCORE_REGEX, message = INVALID_EPIMS_ID)
    String epimmsId;

    @DataField(pos = 2, columnName = "Site_Name")
    @NotBlank
    @Size(max = 256)
    String siteName;

    @DataField(pos = 3, columnName = "Court_Name")
    @Size(max = 256)
    String courtName;

    @DataField(pos = 4, columnName = "Court_Status")
    @Size(max = 32)
    String courtStatus;

    @DataField(pos = 5, columnName = "Court_Open_Date")
    @DatePattern(isNullAllowed = "true", regex = DATE_ONLY_PATTERN,
        message = "date pattern should be " + DATE_ONLY_FORMAT)
    String courtOpenDate;

    @DataField(pos = 6, columnName = "Region_ID")
    @Size(max = 16)
    String regionId;

    @DataField(pos = 7, columnName = "Court_Type_ID")
    @Size(max = 16)
    String courtTypeId;

    @DataField(pos = 8, columnName = "Cluster_ID")
    @Size(max = 16)
    String clusterId;

    @DataField(pos = 9, columnName = "Open_For_Public")
    @NotBlank
    @Pattern(regexp = "(?i)Yes|No", message = "Open_For_Public must be Yes or No")
    @Size(max = 5)
    String openForPublic;

    @DataField(pos = 10, columnName = "Court_Address")
    @NotBlank
    @Size(max = 512)
    String courtAddress;

    @DataField(pos = 11, columnName = "Postcode")
    @NotBlank
    @Size(max = 8)
    String postcode;

    @DataField(pos = 12, columnName = "Phone_Number")
    @Pattern(regexp = PHONE_OR_BLANK_REGEX, message = INVALID_PHONE)
    @Size(max = 128)
    String phoneNumber;

    @DataField(pos = 13, columnName = "Closed_Date")
    @DatePattern(isNullAllowed = "true", regex = DATE_ONLY_PATTERN,
        message = "date pattern should be " + DATE_ONLY_FORMAT)
    String closedDate;

    @DataField(pos = 14, columnName = "Court_Location_Code")
    @Size(max = 8)
    String courtLocationCode;

    @DataField(pos = 15, columnName = "Dx_Address")
    @Size(max = 64)
    String dxAddress;

    @DataField(pos = 16, columnName = "Welsh_Site_Name")
    @Size(max = 256)
    String welshSiteName;

    @DataField(pos = 17, columnName = "Welsh_Court_Address")
    @Size(max = 512)
    String welshCourtAddress;

    @DataField(pos = 18, columnName = "Venue_Name")
    @Size(max = 256)
    String venueName;

    @DataField(pos = 19, columnName = "Is_Case_Management_Location")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_CASE_MANAGEMENT_LOCATION + " " + INVALID_FLAG)
    @Size(max = 1)
    String isCaseManagementLocation;

    @DataField(pos = 20, columnName = "Is_Hearing_Location")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_HEARING_LOCATION + " " + INVALID_FLAG)
    @Size(max = 1)
    String isHearingLocation;

    @DataField(pos = 21, columnName = "Welsh_Venue_Name")
    @Size(max = 256)
    String welshVenueName;

    @DataField(pos = 22, columnName = "Is_Temporary_Location")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_TEMPORARY_LOCATION + " " + INVALID_FLAG)
    @Size(max = 1)
    String isTemporaryLocation;

    @DataField(pos = 23, columnName = "Is_Nightingale_Court")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_NIGHTINGALE_COURT + " " + INVALID_FLAG)
    @Size(max = 1)
    String isNightingaleCourt;

    @DataField(pos = 24, columnName = "Location_Type")
    @Size(max = 16)
    String locationType;

    @DataField(pos = 25, columnName = "Parent_Location")
    @Size(max = 16)
    String parentLocation;

    @DataField(pos = 26, columnName = "Welsh_Court_Name")
    @Size(max = 256)
    String welshCourtName;

    @DataField(pos = 27, columnName = "UPRN")
    @Size(max = 16)
    String uprn;

    @DataField(pos = 28, columnName = "Venue_OU_Code")
    @Size(max = 16)
    String venueOuCode;

    @DataField(pos = 29, columnName = "MRD_Building_Location_ID")
    @Size(max = 16)
    String mrdBuildingLocationId;

    @DataField(pos = 30, columnName = "MRD_Venue_ID")
    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = MRD_VENUE_ID_REGEX, message = INVALID_MRD_VENUE_ID)
    String mrdVenueId;

    @DataField(pos = 31, columnName = "Service_URL")
    @Pattern(regexp = URL_OR_BLANK_REGEX, message = INVALID_URL)
    @Size(max = 1024)
    String serviceUrl;

    @DataField(pos = 32, columnName = "FACT_URL")
    @Pattern(regexp = URL_OR_BLANK_REGEX, message = INVALID_URL)
    @Size(max = 1024)
    String factUrl;

    @DataField(pos = 33, columnName = "MRD_Created_Time")
    @DatePattern(isNullAllowed = "true", regex = DATE_PATTERN,
        message = "date pattern should be " + DATE_TIME_FORMAT)
    String mrdCreatedTime;

    @DataField(pos = 34, columnName = "MRD_Updated_Time")
    @DatePattern(isNullAllowed = "true", regex = DATE_PATTERN,
        message = "date pattern should be " + DATE_TIME_FORMAT)
    String mrdUpdatedTime;

    @DataField(pos = 35, columnName = "MRD_Deleted_Time")
    @DatePattern(isNullAllowed = "true", regex = DATE_PATTERN,
        message = "date pattern should be " + DATE_TIME_FORMAT)
    String mrdDeletedTime;

    @DataField(pos = 36, columnName = "External_Short_Name")
    @Size(max = 80)
    String externalShortName;

    @DataField(pos = 37, columnName = "Welsh_External_Short_Name")
    @Size(max = 80)
    String welshExternalShortName;

    @DataField(pos = 38, columnName = "ServiceID")
    @Pattern(regexp = SERVICE_ID_REGEX, message = INVALID_SERVICE_ID)
    @Size(max = 16)
    String serviceCode;

    @DataField(pos = 39, columnName = "Contact_Email")
    @Pattern(regexp = EMAIL_OR_BLANK_REGEX, message = INVALID_EMAIL)
    @Size(max = 1024)
    String contactEmail;

    @DataField(pos = 40, columnName = "Breathing_Space_Email")
    @Pattern(regexp = EMAIL_OR_BLANK_REGEX, message = INVALID_EMAIL)
    @Size(max = 1024)
    String breathingSpaceEmail;

    @DataField(pos = 41, columnName = "Is_District_Registry")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_DISTRICT_REGISTRY + " " + INVALID_FLAG)
    @Size(max = 1)
    String isDistrictRegistry;

    @DataField(pos = 42, columnName = "Is_Appeal_Centre")
    @Pattern(regexp = YES_NO_BLANK_REGEX, message = IS_APPEAL_CENTRE + " " + INVALID_FLAG)
    @Size(max = 1)
    String isAppealCentre;

    @DataField(pos = 43, columnName = "District_Registry_Venue_ID")
    @Size(max = 16)
    String districtRegistryVenueId;

    @DataField(pos = 44, columnName = "Appeal_Centre_Venue_ID")
    @Size(max = 16)
    String appealCentreVenueId;

    @DataField(pos = 45, columnName = "District_Registry_Site_Name")
    @Size(max = 256)
    String districtRegistrySiteName;

    @DataField(pos = 46, columnName = "District_Registry_Welsh_Site_Name")
    @Size(max = 256)
    String districtRegistryWelshSiteName;

    @DataField(pos = 47, columnName = "District_Registry_External_Short_Name")
    @Size(max = 80)
    String districtRegistryExternalShortName;

    @DataField(pos = 48, columnName = "District_Registry_Welsh_External_Short_Name")
    @Size(max = 80)
    String districtRegistryWelshExternalShortName;
}
