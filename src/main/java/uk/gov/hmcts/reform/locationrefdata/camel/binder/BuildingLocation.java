package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@Getter
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true, skipField = true)
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BuildingLocation {

    @DataField(pos = 1, columnName = "ePIMS_ID")
    @NotBlank
    private String epimsId;

    @DataField(pos = 2, columnName = "Building_Location_Name")
    @NotBlank
    private String buildingLocationName;

    @DataField(pos = 3, columnName = "Status")
    private String status;

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
