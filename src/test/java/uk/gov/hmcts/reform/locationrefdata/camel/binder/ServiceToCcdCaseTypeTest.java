package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceToCcdCaseTypeTest {

    @Test
    public void testServiceToCcdCaseTypeBuilder() {

        ServiceToCcdCaseType serviceToCcdCaseType =
            ServiceToCcdCaseType.builder()
                .ccdCaseType("ccdCaseType")
                .ccdServiceName("ccdServiceName")
                .serviceCode("serviceCode")
                .build();

        assertThat(serviceToCcdCaseType.getCcdCaseType()).isEqualTo("ccdCaseType");
        assertThat(serviceToCcdCaseType.getCcdServiceName()).isEqualTo("ccdServiceName");
        assertThat(serviceToCcdCaseType.getServiceCode()).isEqualTo("serviceCode");

        String serviceToCcdCaseTypeString =
            ServiceToCcdCaseType.builder()
                .ccdCaseType("ccdCaseType")
                .ccdServiceName("ccdServiceName")
                .serviceCode("serviceCode")
                .toString();

        assertThat(serviceToCcdCaseTypeString)
            .isEqualTo("ServiceToCcdCaseType.ServiceToCcdCaseTypeBuilder(serviceCode=serviceCode, "
                           + "ccdServiceName=ccdServiceName, ccdCaseType=ccdCaseType)");
    }

}
