package uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport;

import org.apache.camel.CamelContext;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.data.ingestion.DataIngestionLibraryRunner;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ArchiveFileProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ExceptionProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.route.DataLoadRoute;
import uk.gov.hmcts.reform.data.ingestion.camel.service.AuditServiceImpl;
import uk.gov.hmcts.reform.data.ingestion.camel.service.IEmailService;
import uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.ServiceToCcdCaseType;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdOrgServiceMappingRouteTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;

@ExtendWith(SpringExtension.class)
public abstract class LrdIntegrationBaseTest {

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    @Qualifier("springJdbcTemplate")
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected DataLoadRoute parentRoute;

    @Value("${start-route}")
    protected String startRoute;


    @Value("${archival-cred}")
    protected String archivalCred;

    @Value("${lrd-select-sql}")
    protected String lrdSelectData;

    @Value("${lrd-court-venue-select-sql}")
    protected String lrdCourtVenueSelectData;

    @Value("${audit-enable}")
    protected Boolean auditEnable;

    @Autowired
    protected DataLoadUtil dataLoadUtil;

    @Autowired
    protected ExceptionProcessor exceptionProcessor;

    @Autowired
    protected IEmailService emailService;

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Value("${exception-select-query}")
    protected String exceptionQuery;

    @Value("${ordered-exception-select-query}")
    protected String orderedExceptionQuery;

    @Value("${select-dataload-scheduler}")
    protected String auditSchedulerQuery;

    @Autowired
    protected LrdBlobSupport lrdBlobSupport;

    @Autowired
    protected DataIngestionLibraryRunner dataIngestionLibraryRunner;

    @Autowired
    protected AuditServiceImpl auditService;

    @Autowired
    protected ArchiveFileProcessor archiveFileProcessor;

    @Autowired
    protected LrdOrgServiceMappingRouteTask lrdOrgServiceMappingRouteTask;

    public static final String UPLOAD_ORG_SERVICE_FILE_NAME = "service-test.csv";
    public static final String UPLOAD_COURT_FILE_NAME = "court-venue-test.csv";

    @BeforeEach
    public void setUpSpringContext() throws Exception {
        new TestContextManager(getClass()).prepareTestInstance(this);
        TestContextManager testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
        SpringStarter.getInstance().init(testContextManager);
    }

    @BeforeAll
    public static void beforeAll() {
        if ("preview".equalsIgnoreCase(System.getenv("execution_environment"))) {
            System.setProperty("azure.storage.account-key", System.getenv("BLOB_ACCOUNT_KEY"));
            System.setProperty("azure.storage.account-name", System.getenv("BLOB_ACCOUNT_NAME"));
        } else {
            System.setProperty("azure.storage.account-key", System.getenv("ACCOUNT_KEY"));
            System.setProperty("azure.storage.account-name", System.getenv("ACCOUNT_NAME"));
        }
        System.setProperty("azure.storage.container-name", "lrd-ref-data");
    }

    protected void setLrdCamelRouteToExecute(String route) {
        var routes = new ArrayList<>();
        routes.add(route);
        ReflectionTestUtils.setField(lrdOrgServiceMappingRouteTask, "routesToExecute", routes);
    }

    protected void setLrdFileToLoad(String fileName) {
        var archivalRoutes = new ArrayList<>();
        archivalRoutes.add(fileName);
        ReflectionTestUtils.setField(auditService, "archivalFileNames", archivalRoutes);
        ReflectionTestUtils.setField(archiveFileProcessor, "archivalFileNames", archivalRoutes);
    }

    protected void validateLrdServiceFile(JdbcTemplate jdbcTemplate, String serviceSql,
                                          List<ServiceToCcdCaseType> exceptedResult, int size) {
        var rowMapper = newInstance(ServiceToCcdCaseType.class);
        var serviceToCcdServices = jdbcTemplate.query(serviceSql, rowMapper);
        assertEquals(size, serviceToCcdServices.size());
        assertEquals(exceptedResult, serviceToCcdServices);
    }

    protected void validateLrdCourtVenueFile(JdbcTemplate jdbcTemplate, String courtVenueSql,
                                             List<CourtVenue> expectedResult, int size) {
        var rowMapper = newInstance(CourtVenue.class);
        var courtVenues = jdbcTemplate.query(courtVenueSql, rowMapper);
        assertEquals(size, courtVenues.size());
        courtVenues.forEach(this::processCourtVenue);
        assertEquals(expectedResult, courtVenues);
    }

    private void processCourtVenue(CourtVenue courtVenue) {
        if (courtVenue.getOpenForPublic().equalsIgnoreCase("t")) {
            courtVenue.setOpenForPublic("Yes");
        } else {
            courtVenue.setOpenForPublic("No");
        }
    }


    protected void validateLrdServiceFileAudit(JdbcTemplate jdbcTemplate,
                                               String auditSchedulerQuery, String status, String fileName) {
        var result = jdbcTemplate.queryForList(auditSchedulerQuery);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).get("status"));
        assertEquals(fileName, result.get(0).get("file_name"));
    }

    @SuppressWarnings("unchecked")
    protected void validateLrdServiceFileJsrException(JdbcTemplate jdbcTemplate,
                                                      String exceptionQuery, int size,
                                                      Triplet<String, String, String>... triplets) {
        var result = jdbcTemplate.queryForList(exceptionQuery);
        assertEquals(result.size(), size);
        int index = 0;
        for (Triplet<String, String, String> triplet : triplets) {
            assertEquals(triplet.getValue0(), result.get(index).get("field_in_error"));
            assertEquals(triplet.getValue1(), result.get(index).get("error_description"));
            assertEquals(triplet.getValue2(), result.get(index).get("key"));
            index++;
        }
    }

    @SuppressWarnings("unchecked")
    protected void validateLrdServiceFileException(JdbcTemplate jdbcTemplate,
                                                   String exceptionQuery,
                                                   Pair<String, String> pair) {
        var result = jdbcTemplate.queryForList(exceptionQuery);
        var lastIndex = (result.size() > 1) ? 2 : 1;
        assertThat(
            (String) result.get(result.size() - lastIndex).get("error_description"),
            containsString(pair.getValue1())
        );
    }


    protected Timestamp getTime(String sql, String serviceCode, String caseType) {
        return jdbcTemplate.queryForObject(sql, new Object[]{serviceCode, caseType}, Timestamp.class);
    }
}
