package uk.gov.hmcts.reform.locationrefdata.cameltest;

import com.google.common.collect.ImmutableList;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${start-route}")
    private String startRoute;

    @Value("${archival-route}")
    String archivalRoute;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    private static final String ROUTE_TO_EXECUTE = "lrd-court-venue-load";

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_COURT_FILE_NAME);
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
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId(9).courtTypeId(17)
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build(),
            CourtVenue.builder().epimmsId("123456").siteName("B Tribunal Hearing Centre")
                .courtName("B TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId(9).courtTypeId(31)
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_COURT_FILE_NAME);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_COURT_FILE_NAME);
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
                .courtName("A TRIBUNAL HEARING CENTRE").courtStatus("Open").regionId(9).courtTypeId(17)
                .openForPublic("Yes").courtAddress("AB1,48 COURT STREET,LONDON").postcode("AB12 3AB")
                .build()
        ), 1);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "PartialSuccess", UPLOAD_COURT_FILE_NAME);
        Triplet<String, String, String> triplet1 =
            with("epimmsId", "Epims id is invalid - can contain only alphanumeric characters", "");
        Triplet<String, String, String> triplet2 = with("epimmsId", "must not be blank", "");
        validateLrdServiceFileJsrException(jdbcTemplate, orderedExceptionQuery, 2, triplet1, triplet2);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_COURT_FILE_NAME);
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
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_COURT_FILE_NAME);
        lrdBlobSupport.deleteBlob(UPLOAD_COURT_FILE_NAME);
    }
}
