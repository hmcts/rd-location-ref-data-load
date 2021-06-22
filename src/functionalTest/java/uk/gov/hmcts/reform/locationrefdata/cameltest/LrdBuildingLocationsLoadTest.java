package uk.gov.hmcts.reform.locationrefdata.cameltest;


import com.google.common.collect.ImmutableList;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelTestContextBootstrapper;
import org.apache.camel.test.spring.junit5.MockEndpoints;
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
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.ServiceToCcdCaseType;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.LrdIntegrationBaseTest;
import uk.gov.hmcts.reform.locationrefdata.cameltest.testsupport.SpringStarter;
import uk.gov.hmcts.reform.locationrefdata.config.LrdCamelConfig;
import uk.gov.hmcts.reform.locationrefdata.configuration.BatchConfig;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.jdbc.core.BeanPropertyRowMapper.newInstance;
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
public class LrdBuildingLocationsLoadTest extends LrdIntegrationBaseTest {

    @Value("${lrd-building-location-select-query}")
    protected String lrdBuildingLocationSelectQuery;

    @Value("${start-route}")
    private String startRoute;

    @Value("${archival-route}")
    String archivalRoute;

    @Autowired
    @Qualifier("springJdbcTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    private static final String UPLOAD_FILE_NAME = "building_location_test.csv";

    @BeforeEach
    public void init() {
        SpringStarter.getInstance().restart();
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
    }

    @Test
    @DisplayName("Test for loading a valid CSV file in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCSV() throws Exception {
        testBuildingLocationInsertion(UPLOAD_FILE_NAME);
    }

    @Test
    @DisplayName("Test for loading a valid CSV file in to the building_location table "
        + "which already has a few entries in it")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testAppendBuildingLocation() throws Exception {
        testBuildingLocationInsertion(UPLOAD_FILE_NAME);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        jdbcTemplate.update("delete from DATALOAD_SCHEDULAR_AUDIT");
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        platformTransactionManager.commit(status);
        SpringStarter.getInstance().restart();
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/buildingLocations/building_location_test_2.csv"))
        );
        camelContext.getGlobalOptions()
            .put(SCHEDULER_START_TIME, String.valueOf(new Date(System.currentTimeMillis()).getTime()));

        jobLauncherTestUtils.launchJob();

        validateBuildingLocationFileLoad(ImmutableList.of(
            BuildingLocation.builder().epimsId("219164")
                .buildingLocationName("ABERDEEN TRIBUNAL HEARING CENTRE")
                .postcode("AB116LT")
                .address("AB1, 48 HUNTLY STREET, ABERDEEN")
                .build(),
            BuildingLocation.builder().epimsId("827534")
                .buildingLocationName("ABERYSTWYTH JUSTICE CENTRE")
                .postcode("SY231AS")
                .address("TREFECHANY Lanfa  Trefechan  Aberystwyth")
                .build(),
            BuildingLocation.builder().epimsId("450049")
                .buildingLocationName("ALDERSHOT JUSTICE CENTRE")
                .postcode("GU111NY")
                .address("THE COURT HOUSE, CIVIC CENTRE, WELLINGTON AVENUE")
                .build(),
            BuildingLocation.builder().epimsId("364992")
                .buildingLocationName("ALDGATE TOWER (3RD FLOOR)")
                .postcode("E1 8FA")
                .address("3rd Floor Aldgate Tower 2 Leman Street, LONDON")
                .build()
        ), 4);
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery,
                                    "Success", "building_location_test_2.csv");
        lrdBlobSupport.deleteBlob("building_location_test_2.csv");
    }

    @Test
    @DisplayName("Test for loading a valid CSV file which has data not enclosed within quotes"
        + " in to a clean building_location table")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCSVWithoutQuotes() throws Exception {
        testBuildingLocationInsertion("building_location_test_without_quotes.csv");
    }

    @Test
    @DisplayName("Test for loading a valid CSV file which has entries missing a few non-mandatory fields")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testBuildingLocationCSV_WithNoRegionAndCluster() throws Exception {
        testBuildingLocationInsertion("building_location_test_no_region_cluster.csv");
    }

    @Test
    @DisplayName("Test for loading a valid CSV file which has a combination of valid entries and entries missing"
        + " a mandatory field")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCSV_WithMissingMandatoryValue() throws Exception {
        testBuildingLocationInsertion("building_location_test_no_postcode.csv");
    }

    @Test
    @DisplayName("Test for loading a valid CSV file which has a combination of valid entries and entries missing"
        + " the primary key")
    @Sql({"/testData/truncate-building-locations.sql"})
    public void testLoadValidBuildingLocationCSV_WithMissingEpimsId() throws Exception {
        testBuildingLocationInsertion("building_location_test_no_epims_id.csv");
    }

    private void testBuildingLocationInsertion(String fileName) throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                String.format("classpath:sourceFiles/buildingLocations/%s.csv", fileName)))
        );

        jobLauncherTestUtils.launchJob();
        //Validate Success Result
        validateBuildingLocationFileLoad(ImmutableList.of(
            BuildingLocation.builder().epimsId("219164")
                .buildingLocationName("ABERDEEN TRIBUNAL HEARING CENTRE")
                .postcode("AB116LT")
                .address("AB1, 48 HUNTLY STREET, ABERDEEN")
                .build(),
            BuildingLocation.builder().epimsId("827534")
                .buildingLocationName("ABERYSTWYTH JUSTICE CENTRE")
                .postcode("SY231AS")
                .address("TREFECHANY Lanfa  Trefechan  Aberystwyth")
                .build()
        ), 2);
        //Validates Success Audit
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", fileName);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(fileName);
    }

    private void validateBuildingLocationFileLoad(List<BuildingLocation> expectedBuildingLocationList, int size) {
        var rowMapper = newInstance(BuildingLocation.class);
        var actualBuildingLocationList =
            jdbcTemplate.query(lrdBuildingLocationSelectQuery, rowMapper);
        assertThat(actualBuildingLocationList).hasSize(size).hasSameElementsAs(expectedBuildingLocationList);
    }

}
