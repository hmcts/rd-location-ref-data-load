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
import uk.gov.hmcts.reform.data.ingestion.camel.domain.CommonCsvField;

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
@EqualsAndHashCode(callSuper = false)
public class BuildingLocation extends CommonCsvField {

    @DataField(pos = 1, columnName = "ePIMS_ID")
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_UNDERSCORE_REGEX, message = INVALID_EPIMS_ID)
    private String epimmsId;

    @DataField(pos = 2, columnName = "Building_Location_Name")
    @NotBlank
    private String buildingLocationName;

    @DataField(pos = 3, columnName = "Building_Location_Status")
    private String buildingLocationStatus;

    @DataField(pos = 4, columnName = "Area")
    private String area;

    @DataField(pos = 5, columnName = "Region_ID")
    private String regionId;

    @DataField(pos = 6, columnName = "Cluster_ID")
    private String clusterId;

    @DataField(pos = 7, columnName = "Court_Finder_URL")
    private String courtFinderUrl;

    @DataField(pos = 8, columnName = "Postcode")
    @NotBlank
    private String postcode;

    @DataField(pos = 9, columnName = "Address")
    @NotBlank
    private String address;

}
