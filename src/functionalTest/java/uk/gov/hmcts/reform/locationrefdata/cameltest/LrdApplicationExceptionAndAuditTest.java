package uk.gov.hmcts.reform.locationrefdata.cameltest;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.data.ingestion.configuration.BlobStorageCredentials;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.ServiceToCcdCaseType;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.LrdIntegrationBaseTest;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.SpringStarter;
import uk.gov.hmcts.reform.locationrefdata.config.LrdCamelConfig;
import uk.gov.hmcts.reform.locationrefdata.configuration.BatchConfig;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.ResourceUtils.getFile;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;

@TestPropertySource(properties = "spring.config.location=classpath:application-integration.yml")

@CamelSpringBootTest
@MockEndpoints("log:*")
@ContextConfiguration(classes = {LrdCamelConfig.class, CamelTestContextBootstrapper.class,
    BatchConfig.class, AzureBlobConfig.class, BlobStorageCredentials.class},
    initializers = ConfigDataApplicationContextInitializer.class)
@SpringBatchTest
@SpringBootTest
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
@EnableTransactionManagement
@SqlConfig(dataSource = "dataSource", transactionManager = "txManager",
    transactionMode = SqlConfig.TransactionMode.ISOLATED)
@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Functional")})
@SuppressWarnings("unchecked")
class LrdApplicationExceptionAndAuditTest extends LrdIntegrationBaseTest {

    private static final String SERVICE_CCD_ASSOC_TABLE_NAME = "service_to_ccd_case_type_assoc";

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    public void testTaskletPartialSuccessAndJsr() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-partial-success.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdServiceFile(jdbcTemplate, lrdSelectData, List.of(
            ServiceToCcdCaseType.builder().ccdCaseType("service1")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service2")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_ORG_SERVICE_FILE_NAME);
        Quartet<String, String, String, Long> quartet = Quartet.with("serviceCode", "must not be blank", "", 3L);
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 3, SERVICE_CCD_ASSOC_TABLE_NAME, quartet);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-failure.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var serviceToCcdServices = jdbcTemplate.queryForList(lrdSelectData);
        assertEquals(0, serviceToCcdServices.size());

        Pair<String, String> pair = new Pair<>(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            "ServiceToCcdService failed as no valid records present"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 1);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_ORG_SERVICE_FILE_NAME);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletFailureForInvalidService() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-invalid-service-failure.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var serviceToCcdServices = jdbcTemplate.queryForList(lrdSelectData);
        assertEquals(0, serviceToCcdServices.size());

        Pair<String, String> pair = new Pair<>(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            "violates foreign key constraint"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 0);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_ORG_SERVICE_FILE_NAME);

    }

    @AfterEach
    void tearDown() throws Exception {
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_ORG_SERVICE_FILE_NAME);
    }
}
