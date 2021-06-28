package uk.gov.hmcts.reform.locationrefdata.cameltest;


import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
import static org.javatuples.Triplet.with;
import static org.junit.Assert.assertEquals;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;
import static org.springframework.util.ResourceUtils.getFile;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;

@TestPropertySource(properties = {"spring.config.location=classpath:application-integration.yml"})
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
@WithTags({@WithTag("testType:Functional")})
@SuppressWarnings("unchecked")
public class LrdBuildingLocationsLoadTest extends LrdIntegrationBaseTest {

    @Value("${lrd-building-location-select-query}")
    protected String lrdBuildingLocationSelectQuery;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    private static final String UPLOAD_FILE_NAME = "building_location_test.csv";
    private static final String ROUTE_TO_EXECUTE = "lrd-building-location-load";

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsv_Success() throws Exception {
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        testBuildingLocationInsertion(UPLOAD_FILE_NAME,
                                      MappingConstants.SUCCESS);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file which has headers and data enclosed within quotes"
        + " in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsvWithQuotes_Success() throws Exception {
        String fileName = "building_location_success_test_with_quotes.csv";
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.SUCCESS);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file which has entries "
        + "missing a few non-mandatory fields")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testBuildingLocationCsv_WithNoRegionAndCluster_Success() throws Exception {
        String fileName = "building_location_success_test_no_region_cluster.csv";
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);

        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                String.format("classpath:sourceFiles/buildingLocations/%s", fileName)))
        );
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateBuildingLocationFileLoad(ImmutableList.of(
            BuildingLocation.builder()
                .epimmsId("219164")
                .buildingLocationName("ABERDEEN TRIBUNAL HEARING CENTRE")
                .postcode("AB116LT")
                .address("AB1, 48 HUNTLY STREET, ABERDEEN")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberdeen-employment-tribunal")
                .build(),
            BuildingLocation.builder()
                .epimmsId("827534")
                .buildingLocationName("ABERYSTWYTH JUSTICE CENTRE")
                .postcode("SY231AS")
                .address("TREFECHANY Lanfa  Trefechan  Aberystwyth")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberystwyth-justice-centre")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, MappingConstants.SUCCESS, UPLOAD_FILE_NAME);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of "
        + "valid entries and entries missing a mandatory field")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsv_WithMissingMandatoryValue_PartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_no_postcode.csv";
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Triplet<String, String, String> triplet = with("postcode", "must not be blank", "8275345");
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 1, triplet);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of"
        + "  valid entries and entries missing the primary key")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsv_WithMissingEpimsId_PartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_no_epims_id.csv";
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Triplet[] triplets = {
            with("epimmsId", "must match \"[0-9a-zA-Z_]+\"", ""),
            with("epimmsId", "must not be blank", "")
        };
        validateLrdBuildingLocationFileJsrException(jdbcTemplate, exceptionQuery, 2, triplets);
    }

    @Test
    @DisplayName("Status: PartialSuccess - Test for loading a valid Csv file which has a combination of"
        + "  valid entries and entries. Invalid entry has wrong pattern in the epims id column")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCsv_WithInvalidEpimsId_PartialSuccess() throws Exception {
        String fileName = "building_location_partial_success_test_invalid_epims_id.csv";
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        testBuildingLocationInsertion(fileName,
                                      MappingConstants.PARTIAL_SUCCESS);
        Triplet<String, String, String> triplet = with("epimmsId", "must match \"[0-9a-zA-Z_]+\"", "e-827534");
        validateLrdServiceFileJsrException(jdbcTemplate, exceptionQuery, 1, triplet);
    }

    @Test
    @DisplayName("Status: Success - Test for loading a valid Csv file in to the building_location table "
        + "which already has a few entries in it")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testAppendBuildingLocation_Success() throws Exception {
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        testBuildingLocationInsertion(UPLOAD_FILE_NAME,
                                      MappingConstants.SUCCESS);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        jdbcTemplate.update("delete from DATALOAD_SCHEDULAR_AUDIT");
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);

        SpringStarter.getInstance().restart();

        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);

        String buildingLocationSecondFile = "building_location_test_2.csv";
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(String.format(
                "classpath:sourceFiles/buildingLocations/%s", buildingLocationSecondFile))
            ));
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));

        jobLauncherTestUtils.launchJob();

        validateBuildingLocationFileLoad(ImmutableList.of(
            BuildingLocation.builder()
                .epimmsId("219164")
                .buildingLocationName("ABERDEEN TRIBUNAL HEARING CENTRE")
                .postcode("AB116LT")
                .address("AB1, 48 HUNTLY STREET, ABERDEEN")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .regionId(9)
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberdeen-employment-tribunal")
                .build(),
            BuildingLocation.builder()
                .epimmsId("827534")
                .buildingLocationName("ABERYSTWYTH JUSTICE CENTRE")
                .postcode("SY231AS")
                .address("TREFECHANY Lanfa  Trefechan  Aberystwyth")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .regionId(8)
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberystwyth-justice-centre")
                .build(),
            BuildingLocation.builder().epimmsId("450049")
                .buildingLocationName("ALDERSHOT JUSTICE CENTRE")
                .postcode("GU111NY")
                .address("THE COURT HOUSE, CIVIC CENTRE, WELLINGTON AVENUE")
                .buildingLocationStatus("OPEN")
                .area("SOUTH")
                .regionId(7)
                .clusterId(9)
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aldershot-magistrates-court")
                .build(),
            BuildingLocation.builder().epimmsId("364992")
                .buildingLocationName("ALDGATE TOWER (3RD FLOOR)")
                .postcode("E1 8FA")
                .address("3rd Floor Aldgate Tower 2 Leman Street, LONDON")
                .buildingLocationStatus("OPEN")
                .area("SOUTH")
                .regionId(2)
                .courtFinderUrl("")
                .build()
        ), 4);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_FILE_NAME);
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with an additional unknown header")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationUnknownHeader_Failure() throws Exception {
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/"
                    + "building_location_failure_test_unknown_header.csv"))
        );

        jobLauncherTestUtils.launchJob();
        var buildingLocations = jdbcTemplate.queryForList(lrdBuildingLocationSelectQuery);
        assertEquals(buildingLocations.size(), 0);

        Pair<String, String> pair = new Pair<>(
            UPLOAD_FILE_NAME,
            "Mismatch headers in csv for ::building_location_test.csv"
        );
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_FILE_NAME);
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Status: Failure - Test for loading a file with no valid building locations")
    @Sql(scripts = {"/testData/truncate-building-locations.sql"})
    void testLoadBuildingLocationNoValidRecords_Failure() throws Exception {
        setLrdCamelRouteToExecute(ROUTE_TO_EXECUTE);
        setLrdFileToLoad(UPLOAD_FILE_NAME);
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
        validateLrdServiceFileException(jdbcTemplate, exceptionQuery, pair);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Failure", UPLOAD_FILE_NAME);
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    private void testBuildingLocationInsertion(String fileName, String status) throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(String.format("classpath:sourceFiles/buildingLocations/%s", fileName))));

        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateBuildingLocationFileLoad(ImmutableList.of(
            BuildingLocation.builder()
                .epimmsId("219164")
                .buildingLocationName("ABERDEEN TRIBUNAL HEARING CENTRE")
                .postcode("AB116LT")
                .address("AB1, 48 HUNTLY STREET, ABERDEEN")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .regionId(9)
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberdeen-employment-tribunal")
                .build(),
            BuildingLocation.builder()
                .epimmsId("827534")
                .buildingLocationName("ABERYSTWYTH JUSTICE CENTRE")
                .postcode("SY231AS")
                .address("TREFECHANY Lanfa  Trefechan  Aberystwyth")
                .buildingLocationStatus("OPEN")
                .area("NORTH")
                .regionId(8)
                .courtFinderUrl("https://courttribunalfinder.service.gov.uk/courts/aberystwyth-justice-centre")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, status, UPLOAD_FILE_NAME);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    private void validateBuildingLocationFileLoad(List<BuildingLocation> expectedBuildingLocationList, int size) {
        var rowMapper = newInstance(BuildingLocation.class);
        var actualBuildingLocationList =
            jdbcTemplate.query(lrdBuildingLocationSelectQuery, rowMapper);
        assertThat(actualBuildingLocationList)
            .hasSize(size)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    protected void validateLrdBuildingLocationFileJsrException(JdbcTemplate jdbcTemplate,
                                                      String exceptionQuery, int size,
                                                      Triplet<String, String, String>... triplets) {
        var result = jdbcTemplate.queryForList(exceptionQuery);
        assertEquals(result.size(), size);
        int index = 0;
        for (Triplet<String, String, String> triplet : triplets) {
            assertEquals(triplet.getValue0(), result.get(index).get("field_in_error"));
            assertThat(triplet.getValue1()).isIn("must match \"[0-9a-zA-Z_]+\"", "must not be blank");
            assertEquals(triplet.getValue2(), result.get(index).get("key"));
            index++;
        }
    }

}
