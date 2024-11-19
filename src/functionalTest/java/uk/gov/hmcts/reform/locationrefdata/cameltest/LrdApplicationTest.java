package uk.gov.hmcts.reform.locationrefdata.cameltest;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.data.ingestion.configuration.BlobStorageCredentials;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.ServiceToCcdCaseType;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.LrdIntegrationBaseTest;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.SpringStarter;
import uk.gov.hmcts.reform.locationrefdata.config.LrdCamelConfig;
import uk.gov.hmcts.reform.locationrefdata.configuration.BatchConfig;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
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
@SuppressWarnings("unchecked")
class LrdApplicationTest extends LrdIntegrationBaseTest {

    @Value("${start-route}")
    private String startRoute;

    @Value("${archival-route}")
    String archivalRoute;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletSuccess() throws Exception {
        testInsertion();
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletSuccessWithInsertAndTruncateInsertDay2() throws Exception {
        testInsertion();

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        jdbcTemplate.update("delete from DATALOAD_SCHEDULAR_AUDIT");
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);
        SpringStarter.getInstance().restart();

        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-day2.csv"))
        );
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));

        jobLauncherTestUtils.launchJob();

        validateLrdServiceFile(jdbcTemplate, lrdSelectData, List.of(
            ServiceToCcdCaseType.builder().ccdCaseType("service1")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service3")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service15")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service16")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build()
        ), 4);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_ORG_SERVICE_FILE_NAME);
    }

    private void testInsertion() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdServiceFile(jdbcTemplate, lrdSelectData, List.of(
            ServiceToCcdCaseType.builder().ccdCaseType("service1")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service2")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service11")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service12")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build()
        ), 4);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_ORG_SERVICE_FILE_NAME);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletIdempotent() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test.csv"))
        );
        JobParameters params = new JobParametersBuilder()
            .addString(jobLauncherTestUtils.getJob().getName(), String.valueOf(System.currentTimeMillis()))
            .toJobParameters();
        dataIngestionLibraryRunner.run(jobLauncherTestUtils.getJob(), params);
        SpringStarter.getInstance().restart();

        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-empty-case-or-name.csv"))
        );
        List<Map<String, Object>> auditDetails = jdbcTemplate.queryForList(auditSchedulerQuery);
        final Timestamp timestamp = (Timestamp) auditDetails.get(0).get("scheduler_end_time");
        dataIngestionLibraryRunner.run(jobLauncherTestUtils.getJob(), params);
        //Load result with only files service-test.csv
        validateLrdServiceFile(jdbcTemplate, lrdSelectData, List.of(
            ServiceToCcdCaseType.builder().ccdCaseType("service1")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service2")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service11")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service12")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build()
        ), 4);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_ORG_SERVICE_FILE_NAME);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_ORG_SERVICE_FILE_NAME);
        dataIngestionLibraryRunner.run(jobLauncherTestUtils.getJob(), params);
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-empty-case-or-name.csv"))
        );

        List<Map<String, Object>> auditDetailsNextRun = jdbcTemplate.queryForList(auditSchedulerQuery);
        final Timestamp timestampNextRun = (Timestamp) auditDetailsNextRun.get(0).get("scheduler_end_time");
        assertEquals(timestamp, timestampNextRun);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd.sql"})
    void testTaskletSuccessWithEmptyCaseTypeOrName() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_ORG_SERVICE_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/orgServiceMappings/service-test-empty-case-or-name.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdServiceFile(jdbcTemplate, lrdSelectData, List.of(
            ServiceToCcdCaseType.builder().ccdCaseType("service1")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service2")
                .ccdServiceName("ccd-service1").serviceCode("AAA1").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service11")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build(),
            ServiceToCcdCaseType.builder().ccdCaseType("service12")
                .ccdServiceName("ccd-service2").serviceCode("AAA2").build(),
            ServiceToCcdCaseType.builder()
                .ccdCaseType("service14").serviceCode("AAA4")
                .ccdServiceName(EMPTY).build(),
            ServiceToCcdCaseType.builder()
                .ccdServiceName("ccd-service3").serviceCode("AAA3")
                .ccdCaseType(EMPTY).build()
        ), 6);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_ORG_SERVICE_FILE_NAME);
    }

    @AfterEach
    void tearDown() throws Exception {
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_ORG_SERVICE_FILE_NAME);
    }
}
