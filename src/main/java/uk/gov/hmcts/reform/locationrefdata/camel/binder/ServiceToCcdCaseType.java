package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import jakarta.validation.constraints.NotBlank;
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

@Component
@Setter
@Getter
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true, skipField = true)
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ServiceToCcdCaseType extends CommonCsvField {

    @DataField(pos = 1, columnName = "service_code")
    @NotBlank
    String serviceCode;

    @DataField(pos = 2, columnName = "ccd_service_name")
    String ccdServiceName;

    @DataField(pos = 3, columnName = "ccd_case_type")
    String ccdCaseType;
}
