package uk.gov.hmcts.reform.locationrefdata.cameltest;

import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
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
import uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants;
import uk.gov.hmcts.reform.data.ingestion.configuration.AzureBlobConfig;
import uk.gov.hmcts.reform.data.ingestion.configuration.BlobStorageCredentials;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.LrdIntegrationBaseTest;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.SpringStarter;
import uk.gov.hmcts.reform.locationrefdata.config.LrdCamelConfig;
import uk.gov.hmcts.reform.locationrefdata.configuration.BatchConfig;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.javatuples.Quartet.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;
import static org.springframework.util.ResourceUtils.getFile;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.ADDRESS_ONE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.ADDRESS_TWO;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.AREA_NORTH;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.BUILDING_LOC_ONE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.BUILDING_LOC_STATUS_OPEN;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.BUILDING_LOC_TWO;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.COURT_FINDER_URL_ONE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.COURT_FINDER_URL_TWO;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.EPIMMSID;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.EPIMMSID_FOUR;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.EPIMMSID_ONE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.EPIMMSID_THREE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.EPIMMSID_TWO;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.FAILURE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.POSTCODE_ONE;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.POSTCODE_TWO;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.WELSH_ADDRESS;
import static uk.gov.hmcts.reform.locationrefdata.LrdDataTestConstants.WELSH_BUILDING_LOC_NAME;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.INVALID_EPIMS_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID_NOT_EXISTS;

@TestPropertySource(properties = "spring.config.location=classpath:application-integration.yml")
@CamelSpringBootTest
@MockEndpoints("log:*")
@ContextConfiguration(classes = {LrdCamelConfig.class, CamelTestContextBootstrapper.class,
    JobLauncherTestUtils.class, BatchConfig.class, AzureBlobConfig.class, BlobStorageCredentials.class},
    initializers = ConfigDataApplicationContextInitializer.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
@EnableTransactionManagement
@SqlConfig(dataSource = "dataSource", transactionManager = "txManager",
    transactionMode = SqlConfig.TransactionMode.ISOLATED)
@WithTags({@WithTag("testType:Functional")})
@SuppressWarnings("unchecked")
public class LrdBuildingLocationsLoadTest extends LrdIntegrationBaseTest {

    @Value("${lrd-building-location-select-query}")
    protected String lrdBuildingLocationSelectQuery;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    private static final String BUILDING_LOCATION_TABLE_NAME = "building_location";

    private static final String UPLOAD_FILE_NAME = "building_location_test.csv";

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvSuccess() throws Exception {
        testBuildingLocationInsertion(UPLOAD_FILE_NAME,
                                      MappingConstants.SUCCESS);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file with different headers in different cases "
        + "in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvTestCaseInsensitiveHeadersSuccess() throws Exception {
        String fileName = "building_location_test_success_case_insensitive_headers.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.SUCCESS);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file which has headers and data enclosed within quotes"
        + " in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithQuotesSuccess() throws Exception {
        String fileName = "building_location_success_test_with_quotes.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.SUCCESS);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file which has entries "
        + "missing a few non-mandatory fields")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testBuildingLocationCsvWithNoRegionAndClusterSuccess() throws Exception {
        String fileName = "building_location_success_test_no_region_cluster.csv";

        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                String.format("classpath:sourceFiles/buildingLocations/%s", fileName)))
        );
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateBuildingLocationFileLoad(List.of(
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_ONE)
                .buildingLocationName(BUILDING_LOC_ONE)
                .postcode(POSTCODE_ONE)
                .address(ADDRESS_ONE)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .courtFinderUrl(COURT_FINDER_URL_ONE)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("12345")
                .build(),
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_TWO)
                .buildingLocationName(BUILDING_LOC_TWO)
                .postcode(POSTCODE_TWO)
                .address(ADDRESS_TWO)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .courtFinderUrl(COURT_FINDER_URL_TWO)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("67890")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, MappingConstants.SUCCESS, UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of "
        + "valid entries and entries missing a mandatory field")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithMissingMandatoryValuePartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_no_postcode.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Quartet<String, String, String, Long> quartet = with("postcode", "must not be blank", "8275345", 4L);
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 3, BUILDING_LOCATION_TABLE_NAME,quartet);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of "
        + "valid entries and entries. The invalid row has a non-existing region_id.")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithForeignKeyViolationInRegionPartialSuccess() throws Exception {
        String fileName = "building_location_test_partial_success_non_existent_region_id.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Quartet<String, String, String, Long> quartet = with(REGION_ID, REGION_ID_NOT_EXISTS, "2191645", 4L);
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 3, BUILDING_LOCATION_TABLE_NAME,quartet);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of "
        + "valid entries and entries. The invalid row has a non-existing cluster_id.")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithForeignKeyViolationInClusterPartialSuccess() throws Exception {
        String fileName = "building_location_test_partial_success_non_existent_cluster_id.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Quartet<String, String, String, Long> quartet = with(CLUSTER_ID, CLUSTER_ID_NOT_EXISTS, "8275345", 4L);
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 3, BUILDING_LOCATION_TABLE_NAME,quartet);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of"
        + " valid entries and entries missing the primary key")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithMissingEpimsIdPartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_no_epims_id.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Quartet<String, String, String, Long> quartet1 = with(EPIMMSID, "must not be blank", "", 4L);
        Quartet<String, String, String, Long> quartet2 = with(EPIMMSID, INVALID_EPIMS_ID, "", 4L);

        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 4,
                                           BUILDING_LOCATION_TABLE_NAME, quartet1, quartet2);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of"
        + " valid entries and entries. Invalid entry has wrong pattern in the epims id column")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithInvalidEpimsIdPartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_invalid_epims_id.csv";
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Quartet<String, String, String, Long> quartet = with(EPIMMSID, INVALID_EPIMS_ID, "e-827534", 4L);
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 3,
                                           BUILDING_LOCATION_TABLE_NAME, quartet);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file in to the building_location table "
        + "which already has a few entries in it")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testAppendBuildingLocationSuccess() throws Exception {
        testBuildingLocationInsertion(UPLOAD_FILE_NAME,
                                      MappingConstants.SUCCESS);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        jdbcTemplate.update("delete from DATALOAD_SCHEDULAR_AUDIT");
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);

        SpringStarter.getInstance().restart();

        String buildingLocationSecondFile = "building_location_test_2.csv";
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(String.format(
                "classpath:sourceFiles/buildingLocations/%s", buildingLocationSecondFile))
            ));
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));

        jobLauncherTestUtils.launchJob();

        validateBuildingLocationFileLoad(List.of(
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_ONE)
                .buildingLocationName(BUILDING_LOC_ONE)
                .postcode(POSTCODE_ONE)
                .address(ADDRESS_ONE)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("9")
                .courtFinderUrl(COURT_FINDER_URL_ONE)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("12345")
                .latitude(51.51996519)
                .longitude(-51.51996519)
                .mrdBuildingLocationId("34567")
                .mrdCreatedTime("2020-01-01 00:00:00")
                .mrdUpdatedTime("2021-02-01 10:15:20")
                .mrdDeletedTime("2022-03-01 01:02:03")
                .build(),
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_TWO)
                .buildingLocationName(BUILDING_LOC_TWO)
                .postcode(POSTCODE_TWO)
                .address(ADDRESS_TWO)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("8")
                .courtFinderUrl(COURT_FINDER_URL_TWO)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("67890")
                .latitude(4.345)
                .longitude(511.51996519)
                .mrdBuildingLocationId("64567")
                .mrdCreatedTime("2022-04-01 02:00:01")
                .mrdUpdatedTime("2022-04-01 02:00:02")
                .mrdDeletedTime("2022-04-01 02:00:03")
                .build(),
            BuildingLocation.builder().epimmsId(EPIMMSID_THREE)
                .buildingLocationName("ALDERSHOT JUSTICE CENTRE")
                .postcode("GU111NY")
                .address("THE COURT HOUSE, CIVIC CENTRE, WELLINGTON AVENUE")
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area("SOUTH")
                .regionId("7")
                .clusterId("9")
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aldershot-magistrates-court")
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("12345")
                .build(),
            BuildingLocation.builder().epimmsId(EPIMMSID_FOUR)
                .buildingLocationName("ALDGATE TOWER (3RD FLOOR)")
                .postcode("E1 8FA")
                .address("3rd Floor Aldgate Tower 2 Leman Street, LONDON")
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area("SOUTH")
                .regionId("2")
                .courtFinderUrl("")
                .welshBuildingLocationName("")
                .welshAddress("")
                .uprn("")
                .build()
        ), 4);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with an additional unknown header.")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationUnknownHeaderFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/"
                    + "building_location_failure_test_unknown_header.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var buildingLocations = jdbcTemplate.queryForList(lrdBuildingLocationSelectQuery);
        assertEquals(0, buildingLocations.size());

        Pair<String, String> pair = new Pair<>(
            UPLOAD_FILE_NAME,
            "There is a mismatch in the headers of the csv file :: building_location_test.csv"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 1);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, FAILURE, UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with a missing header.")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationMissingHeaderFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/"
                    + "building_location_test_failure_missing_header.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var buildingLocations = jdbcTemplate.queryForList(lrdBuildingLocationSelectQuery);
        assertEquals(0, buildingLocations.size());

        Pair<String, String> pair = new Pair<>(
            UPLOAD_FILE_NAME,
            "There is a mismatch in the headers of the csv file :: building_location_test.csv"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 1);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, FAILURE, UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with the headers in jumbled order.")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationHeaderInJumbledOrderFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/"
                    + "building_location_test_failure_missing_header.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var buildingLocations = jdbcTemplate.queryForList(lrdBuildingLocationSelectQuery);
        assertEquals(buildingLocations.size(), 0);

        Pair<String, String> pair = new Pair<>(
            UPLOAD_FILE_NAME,
            "There is a mismatch in the headers of the csv file :: building_location_test.csv"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 1);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, FAILURE, UPLOAD_FILE_NAME);

    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with no valid building locations")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationNoValidRecordsFailure() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/"
                    + "building_location_failure_test_no_valid_building_location.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var buildingLocations = jdbcTemplate.queryForList(lrdBuildingLocationSelectQuery);
        assertEquals(buildingLocations.size(), 0);

        Pair<String, String> pair = new Pair<>(
            UPLOAD_FILE_NAME,
            "No valid building locations found in the input file. Please review and try again."
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair, 4);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, FAILURE, UPLOAD_FILE_NAME);

    }

    @Test
    @DisplayName("Status: Success - Test for verifying the idempotent nature of the route. "
        + "Loading the same file twice should not alter the state of the data.")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadBuildingLocationIdempotentSuccess() throws Exception {
        testBuildingLocationInsertion(UPLOAD_FILE_NAME,
                                      MappingConstants.SUCCESS);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        jdbcTemplate.update("delete from DATALOAD_SCHEDULAR_AUDIT");
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);

        SpringStarter.getInstance().restart();

        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(String.format(
                "classpath:sourceFiles/buildingLocations/%s", UPLOAD_FILE_NAME))
            ));
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));

        jobLauncherTestUtils.launchJob();

        validateBuildingLocationFileLoad(List.of(
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_ONE)
                .buildingLocationName(BUILDING_LOC_ONE)
                .postcode(POSTCODE_ONE)
                .address(ADDRESS_ONE)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("9")
                .courtFinderUrl(COURT_FINDER_URL_ONE)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("12345")
                .latitude(51.51996519)
                .longitude(-51.51996519)
                .mrdBuildingLocationId("34567")
                .mrdCreatedTime("2022-04-01 02:00:01")
                .mrdUpdatedTime("2022-04-01 02:00:02")
                .mrdDeletedTime("2022-04-01 02:00:03")
                .build(),
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_TWO)
                .buildingLocationName(BUILDING_LOC_TWO)
                .postcode(POSTCODE_TWO)
                .address(ADDRESS_TWO)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("8")
                .courtFinderUrl(COURT_FINDER_URL_TWO)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("67890")
                .latitude(4.345)
                .longitude(511.51996519)
                .mrdBuildingLocationId("64567")
                .mrdCreatedTime("2022-04-01 02:00:01")
                .mrdUpdatedTime("2022-04-01 02:00:02")
                .mrdDeletedTime("2022-04-01 02:00:03")
                .build()
        ), 2);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_FILE_NAME);

    }

    private void testBuildingLocationInsertion(String fileName, String status) throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(String.format("classpath:sourceFiles/buildingLocations/%s", fileName))));

        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateBuildingLocationFileLoad(List.of(
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_ONE)
                .buildingLocationName(BUILDING_LOC_ONE)
                .postcode(POSTCODE_ONE)
                .address(ADDRESS_ONE)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("9")
                .courtFinderUrl(COURT_FINDER_URL_ONE)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("12345")
                .latitude(51.51996519)
                .longitude(-51.51996519)
                .mrdBuildingLocationId("34567")
                .mrdCreatedTime("2022-04-01 02:00:01")
                .mrdUpdatedTime("2022-04-01 02:00:02")
                .mrdDeletedTime("2022-04-01 02:00:03")
                .build(),
            BuildingLocation.builder()
                .epimmsId(EPIMMSID_TWO)
                .buildingLocationName(BUILDING_LOC_TWO)
                .postcode(POSTCODE_TWO)
                .address(ADDRESS_TWO)
                .buildingLocationStatus(BUILDING_LOC_STATUS_OPEN)
                .area(AREA_NORTH)
                .regionId("8")
                .courtFinderUrl(COURT_FINDER_URL_TWO)
                .welshBuildingLocationName(WELSH_BUILDING_LOC_NAME)
                .welshAddress(WELSH_ADDRESS)
                .uprn("67890")
                .latitude(4.345)
                .longitude(511.51996519)
                .mrdBuildingLocationId("64567")
                .mrdCreatedTime("2022-04-01 02:00:01")
                .mrdUpdatedTime("2022-04-01 02:00:02")
                .mrdDeletedTime("2022-04-01 02:00:03")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, status, UPLOAD_FILE_NAME);
    }

    private void validateBuildingLocationFileLoad(List<BuildingLocation> expectedBuildingLocationList, int size) {
        var rowMapper = newInstance(BuildingLocation.class);
        var actualBuildingLocationList =
            jdbcTemplate.query(lrdBuildingLocationSelectQuery, rowMapper);
        assertThat(actualBuildingLocationList)
            .hasSize(size)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @AfterEach
    void tearDown() throws Exception {
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }
}
