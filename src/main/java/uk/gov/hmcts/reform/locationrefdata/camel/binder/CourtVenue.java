package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.ALPHANUMERIC_UNDERSCORE_REGEX;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_EPIMS_ID;

@Component
@Setter
@Getter
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true, skipField = true)
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourtVenue {

    @DataField(pos = 1, columnName = "ePIMS_id")
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_UNDERSCORE_REGEX, message = INVALID_EPIMS_ID)
    String epimmsId;

    @DataField(pos = 2, columnName = "Site_Name")
    @NotBlank
    String siteName;

    @DataField(pos = 3, columnName = "Court_Name")
    String courtName;

    @DataField(pos = 4, columnName = "Court_Status")
    String courtStatus;

    @DataField(pos = 5, columnName = "Court_Open_Date")
    String courtOpenDate;

    @DataField(pos = 6, columnName = "Region_ID")
    Integer regionId;

    @DataField(pos = 7, columnName = "Court Type ID")
    Integer courtTypeId;

    @DataField(pos = 8, columnName = "Cluster_ID")
    Integer clusterId;

    @DataField(pos = 9, columnName = "Open_For_Public")
    @NotBlank
    String openForPublic;

    @DataField(pos = 10, columnName = "Court_Address")
    @NotBlank
    String courtAddress;

    @DataField(pos = 11, columnName = "Postcode")
    @NotBlank
    String postcode;

    @DataField(pos = 12, columnName = "Phone_Number")
    String phoneNumber;

    @DataField(pos = 13, columnName = "Closed_Date")
    String closedDate;

    @DataField(pos = 14, columnName = "Court_Location_Code")
    String courtLocationCode;

    @DataField(pos = 15, columnName = "Dx_Address")
    String dxAddress;

    @DataField(pos = 16, columnName = "Welsh_Site_Name")
    String welshSiteName;

    @DataField(pos = 17, columnName = "Welsh_Court_Address")
    String welshCourtAddress;

}
