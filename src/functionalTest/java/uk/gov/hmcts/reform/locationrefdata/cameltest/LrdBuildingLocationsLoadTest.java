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
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
    public void testValidBuildingLocationCSV() throws Exception {
        testBuildingLocationInsertion();
    }

    private void testBuildingLocationInsertion() throws Exception {
        lrdBlobSupport.uploadFile(
            UPLOAD_FILE_NAME,
            new FileInputStream(getFile(
                "classpath:sourceFiles/building_location_test.csv"))
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
        validateLrdServiceFileAudit(jdbcTemplate, auditSchedulerQuery, "Success", UPLOAD_FILE_NAME);
        //Delete Uploaded test file with Snapshot delete
        lrdBlobSupport.deleteBlob(UPLOAD_FILE_NAME);
    }

    private void validateBuildingLocationFileLoad(List<BuildingLocation> expectedBuildingLocationList, int size) {
        var rowMapper = newInstance(BuildingLocation.class);
        var actualBuildingLocationList =
            jdbcTemplate.query(lrdBuildingLocationSelectQuery, rowMapper);
        assertThat(actualBuildingLocationList).hasSize(size).hasSameElementsAs(expectedBuildingLocationList);
    }

}
