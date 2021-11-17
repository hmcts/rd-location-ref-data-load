package uk.gov.hmcts.reform.locationrefdata.camel.binder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceToCcdCaseTypeTest {

    @Test
    void testServiceToCcdCaseTypeBuilder() {

        ServiceToCcdCaseType serviceToCcdCaseType =
            ServiceToCcdCaseType.builder()
                .ccdCaseType("ccdCaseType")
                .ccdServiceName("ccdServiceName")
                .serviceCode("serviceCode")
                .build();

        assertEquals("ccdCaseType", serviceToCcdCaseType.getCcdCaseType());
        assertEquals("ccdServiceName", serviceToCcdCaseType.getCcdServiceName());
        assertEquals("serviceCode", serviceToCcdCaseType.getServiceCode());

        String serviceToCcdCaseTypeString =
            ServiceToCcdCaseType.builder()
                .ccdCaseType("ccdCaseType")
                .ccdServiceName("ccdServiceName")
                .serviceCode("serviceCode")
                .toString();

        assertEquals("ServiceToCcdCaseType.ServiceToCcdCaseTypeBuilder(serviceCode=serviceCode, "
                         + "ccdServiceName=ccdServiceName, ccdCaseType=ccdCaseType)",
                     serviceToCcdCaseTypeString);
    }

}
