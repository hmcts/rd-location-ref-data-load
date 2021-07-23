package uk.gov.hmcts.reform.locationrefdata.cameltest;

import com.google.common.collect.ImmutableList;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.data.ingestion.configuration.BlobStorageCredentials;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.LrdIntegrationBaseTest;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.SpringStarter;
import uk.gov.hmcts.reform.locationrefdata.config.LrdCamelConfig;
import uk.gov.hmcts.reform.locationrefdata.configuration.BatchConfig;

import java.io.FileInputStream;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.javatuples.Triplet.with;
import static org.springframework.util.ResourceUtils.getFile;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.COURT_TYPE_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.COURT_TYPE_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.EPIMMS_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.EPIMMS_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_EPIMS_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID_NOT_EXISTS;

@TestPropertySource(properties = {"spring.config.location=classpath:application-integration.yml,"
    + "classpath:application-leaf-integration.yml"})
@CamelSpringBootTest
@MockEndpoints("log:*")
@ContextConfiguration(classes = {LrdCamelConfig.class, CamelTestContextBootstrapper.class,
    JobLauncherTestUtils.class, BatchConfig.class, AzureBlobConfig.class, BlobStorageCredentials.class},
    initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
@EnableTransactionManagement
@SqlConfig(dataSource = "dataSource", transactionManager = "txManager",
    transactionMode = SqlConfig.TransactionMode.ISOLATED)
@SuppressWarnings("unchecked")
public class LrdCourtVenueTest extends LrdIntegrationBaseTest {

    private static final String COURT_VENUE_TABLE_NAME = "court_venue";

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
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTaskletSuccess() throws Exception {
        testCourtVenueInsertion();
    }

    private void testCourtVenueInsertion() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-test.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build(),
            CourtVenue.builder().epimmsId("123456").siteName("B Tribunal Hearing Centre")
                .courtName("B TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("31")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_COURT_FILE_NAME);
    }


    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTaskletPartialSuccess() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-partial-success.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with("epimmsId", INVALID_EPIMS_ID, "");
        Triplet<String, String, String> triplet2 = with("epimmsId", "must not be blank", "");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 4,
                                           COURT_VENUE_TABLE_NAME, triplet1, triplet2);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTasklet_NonexistentRegion_PartialSuccess() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-test-partial-success-non-existent-region.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with(REGION_ID, REGION_ID_NOT_EXISTS, "123456");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 3,
                                           COURT_VENUE_TABLE_NAME, triplet1);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTasklet_NonexistentCluster_PartialSuccess() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-test-partial-success-non-existent-cluster.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with(CLUSTER_ID, CLUSTER_ID_NOT_EXISTS, "123456");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 3,
                                           COURT_VENUE_TABLE_NAME, triplet1);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTasklet_NonexistentCourtTypeId_PartialSuccess() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-test-partial-success-non-existent-court-type-id.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with(COURT_TYPE_ID, COURT_TYPE_ID_NOT_EXISTS, "123456");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 3,
                                           COURT_VENUE_TABLE_NAME, triplet1);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTasklet_NonexistentEpimmsId_PartialSuccess() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-test-partial-success-non-existent-epimms-id.csv"))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateLrdCourtVenueFile(jdbcTemplate, lrdCourtVenueSelectData, ImmutableList.of(
            CourtVenue.builder().epimmsId("123456").siteName("A Tribunal Hearing Centre")
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId("9").courtTypeId("17")
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with(EPIMMS_ID, EPIMMS_ID_NOT_EXISTS, "a123456");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 3,
                                           COURT_VENUE_TABLE_NAME, triplet1);
    }

    @Test
    @Sql(scripts = {"/testData/truncate-lrd-court-venue.sql", "/testData/insert-building-location.sql"})
    void testTaskletFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_COURT_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/court-venue-failure.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var courtVenues = jdbcTemplate.queryForList(lrdCourtVenueSelectData);
        assertThat(courtVenues).hasSize(0);

        Pair<String, String> pair = new Pair<>(
            UPLOAD_COURT_FILE_NAME,
            "Court Venue upload failed as no valid records present"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 5);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_COURT_FILE_NAME);
    }

    @AfterEach
    void tearDown() throws Exception {
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_COURT_FILE_NAME);
    }
}
