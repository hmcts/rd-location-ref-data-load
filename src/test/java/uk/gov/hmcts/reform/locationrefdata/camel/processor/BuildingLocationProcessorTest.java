package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.configuration.DataQualityCheckConfiguration;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ROUTE_DETAILS;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BuildingLocationProcessorTest {

    @Spy
    private BuildingLocationProcessor processor = new BuildingLocationProcessor();

    CamelContext camelContext = new DefaultCamelContext();

    Exchange exchange = new DefaultExchange(camelContext);
    @Spy
    JsrValidatorInitializer<BuildingLocation> buildingLocationJsrValidatorInitializer
        = new JsrValidatorInitializer<>();

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    PlatformTransactionManager platformTransactionManager;

    @Mock
    ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Mock
    ConfigurableApplicationContext applicationContext;

    private static final List<String> ZERO_BYTE_CHARACTERS = List.of("\u200B", " ");

    private static final List<Pair<String, Long>> ZERO_BYTE_CHARACTER_RECORDS = List.of(
        Pair.of("epims1::building 1", null),
            Pair.of("epims2::building 2", null));

    @Mock
    DataQualityCheckConfiguration dataQualityCheckConfiguration = new DataQualityCheckConfiguration();

    @BeforeEach
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        setField(dataQualityCheckConfiguration, "zeroByteCharacters", ZERO_BYTE_CHARACTERS);
        setField(buildingLocationJsrValidatorInitializer, "validator", validator);
        setField(buildingLocationJsrValidatorInitializer, "camelContext", camelContext);
        setField(processor, "jdbcTemplate", jdbcTemplate);
        setField(buildingLocationJsrValidatorInitializer, "jdbcTemplate", jdbcTemplate);
        setField(buildingLocationJsrValidatorInitializer, "platformTransactionManager",
                 platformTransactionManager);
        setField(processor, "buildingLocationJsrValidatorInitializer",
                 buildingLocationJsrValidatorInitializer
        );
        setField(processor, "logComponentName",
                 "testlogger"
        );
        setField(processor, "dataQualityCheckConfiguration",
                 dataQualityCheckConfiguration);
        setField(processor, "regionQuery", "ids");
        setField(processor, "clusterQuery", "ids");
        setField(processor, "applicationContext", applicationContext);
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("test");
        exchange.getIn().setHeader(ROUTE_DETAILS, routeProperties);
    }

    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed."
        + " All the building locations have data in all the fields.")
    void testProcessValidFile() throws Exception {
        List<BuildingLocation> expectedBuildingLocationList = getValidBuildingLocations();

        exchange.getIn().setBody(expectedBuildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed."
        + " All the building locations have data in just the mandatory fields.")
    void testProcessValidFile_HasDataOnlyInMandatoryFields() throws Exception {
        List<BuildingLocation> expectedBuildingLocationList = ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .area("Area ABCD")
                .epimmsId("epims1")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build(),
            BuildingLocation.builder()
                .buildingLocationName("building 2")
                .postcode("E1 23B")
                .address("Address ABCD")
                .epimmsId("epims_2")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );

        exchange.getIn().setBody(expectedBuildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }


    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed"
        + " along with an invalid building location. All the valid building locations have data in all the fields.")
    void testProcessValidFile_CombinationOfValidAndInvalidBuildingLocations() throws Exception {
        var buildingLocationList = new ArrayList<BuildingLocation>();
        buildingLocationList.addAll(getInvalidBuildingLocations());

        List<BuildingLocation> expectedBuildingLocationList = getValidBuildingLocations();
        buildingLocationList.addAll(expectedBuildingLocationList);

        exchange.getIn().setBody(buildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));

        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed"
        + " along with an invalid building location. All the valid building locations have data in all the fields."
        + " The invalid location has a non-existing region id.")
    void testProcessValidFile_CombinationOfValidAndInvalidBuildingLocations_InvalidRegion() throws Exception {
        var buildingLocationList = new ArrayList<BuildingLocation>();
        buildingLocationList.add(
            BuildingLocation.builder()
            .buildingLocationName("building 1")
            .postcode("E1 23A")
            .address("Address ABC")
            .clusterId("123")
            .courtFinderUrl("website url 1")
            .regionId("abc")
            .epimmsId("epims_1")
            .buildingLocationStatus("OPEN")
            .welshBuildingLocationName("welsh building")
            .welshAddress("Welsh Address")
            .uprn("1234")
            .latitude(1111.2222)
            .longitude(3333.4444)
            .mrdBuildingLocationId("98765")
            .build()
        );

        List<BuildingLocation> expectedBuildingLocationList = getValidBuildingLocations();
        buildingLocationList.addAll(expectedBuildingLocationList);

        exchange.getIn().setBody(buildingLocationList);
        when(((ConfigurableApplicationContext)
            applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));

        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed"
        + " along with multiple invalid building location. All the valid building locations have "
        + "data in all the fields. The invalid locations have a non-existing region id and cluster id respectively")
    void testProcessValidFile_CombinationOfValidAndInvalidBuildingLocations_InvalidRegionInvalidCluster()
        throws Exception {

        var buildingLocationList = new ArrayList<BuildingLocation>();
        buildingLocationList.add(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .clusterId("123")
                .courtFinderUrl("website url 1")
                .regionId("abc")
                .epimmsId("epims_1")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );
        buildingLocationList.add(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .clusterId("abc")
                .courtFinderUrl("website url 1")
                .regionId("123")
                .epimmsId("epims_2")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );

        List<BuildingLocation> expectedBuildingLocationList = getValidBuildingLocations();
        buildingLocationList.addAll(expectedBuildingLocationList);

        exchange.getIn().setBody(buildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        when(((ConfigurableApplicationContext)
            applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);

        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @Test
    @DisplayName("Test to check the behaviour when multiple valid building locations are passed"
        + " along with an invalid building location. All the building locations have data in all the fields."
        + " The invalid building location has a missing epims id")
    void testProcessValidFile_CombinationOfValidAndInvalidBuildingLocations_MissingEpimsId() throws Exception {
        var buildingLocationList = new ArrayList<BuildingLocation>();
        buildingLocationList.add(BuildingLocation.builder()
            .buildingLocationName("building location")
            .postcode("postcode")
            .address("address")
            .build());

        List<BuildingLocation> expectedBuildingLocationList = getValidBuildingLocations();
        buildingLocationList.addAll(expectedBuildingLocationList);

        exchange.getIn().setBody(buildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList =
            (List<BuildingLocation>) exchange.getMessage().getBody();

        assertThat(actualBuildingLocationList)
            .hasSize(2)
            .hasSameElementsAs(expectedBuildingLocationList);
    }

    @Test
    @DisplayName("Test to check the behaviour when a single building locations is passed"
        + " and its epims id is invalid (has a special character)")
    void testProcessInvalidFile_SingleRow_InvalidEpimsId() throws Exception {
        exchange.getIn().setBody(getInvalidBuildingLocations());
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));
        verify(processor, times(1)).process(exchange);
    }

    @Test
    @DisplayName("Test to check the behaviour when a single building locations is passed"
        + " and its cluster id is invalid (non-existent)")
    void testProcessInvalidFile_SingleRow_InvalidClusterId() throws Exception {
        var buildingLocationList = ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .clusterId("abc")
                .courtFinderUrl("website url 1")
                .regionId("123")
                .epimmsId("epims_2")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );
        exchange.getIn().setBody(buildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));
        verify(processor, times(1)).process(exchange);
    }

    @Test
    @DisplayName("Test to check the behaviour when a single building locations is passed"
        + " and its region id is invalid (non-existent)")
    void testProcessInvalidFile_SingleRow_InvalidRegionId() throws Exception {
        var buildingLocationList = ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .clusterId("123")
                .courtFinderUrl("website url 1")
                .regionId("abc")
                .epimmsId("epims_2")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );
        exchange.getIn().setBody(buildingLocationList);
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));
        verify(processor, times(1)).process(exchange);
    }

    @Test
    @DisplayName("Test to check the behaviour when a single building locations is passed"
        + " and its epims id is missing")
    void testProcessInvalidFile_SingleRow_NoEpimsId() throws Exception {
        exchange.getIn().setBody(BuildingLocation.builder()
                                    .address("address")
                                    .postcode("postcode")
                                    .buildingLocationName("locationName")
                                    .build());
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));
        verify(processor, times(1)).process(exchange);
    }

    @Test
    @DisplayName("Test to check the behaviour when a single building locations is passed"
        + " and its epims id is empty")
    void testProcessInvalidFile_SingleRow_EmptyEpimsId() throws Exception {
        exchange.getIn().setBody(BuildingLocation.builder()
                                     .address("address")
                                     .postcode("postcode")
                                     .buildingLocationName("locationName")
                                     .epimmsId("")
                                     .build());
        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));
        verify(processor, times(1)).process(exchange);
    }


    @Test
    @DisplayName("Test for 0 byte characters in record")
    void testBuildingLocationsCsv_0byte_characters() throws Exception {
        var buildingLocationList = new ArrayList<BuildingLocation>();
        buildingLocationList.addAll(getZeroByteBuildingLocations());

        exchange.getIn().setBody(buildingLocationList);

        doNothing().when(processor).audit(buildingLocationJsrValidatorInitializer, exchange);
        when((processor).validate(buildingLocationJsrValidatorInitializer,buildingLocationList))
            .thenReturn(buildingLocationList);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("123"));
        when(((ConfigurableApplicationContext)
           applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);

        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<BuildingLocation> actualBuildingLocationList = (List<BuildingLocation>) exchange.getMessage().getBody();

        Assertions.assertEquals(2, actualBuildingLocationList.size());
        verify(buildingLocationJsrValidatorInitializer, times(1))
            .auditJsrExceptions(eq(ZERO_BYTE_CHARACTER_RECORDS),
                                eq(null),
                                eq("Zero byte characters identified - check source file"),
                                eq(exchange));

    }




    private List<BuildingLocation> getInvalidBuildingLocations() {
        return ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .area("Area ABCD")
                .clusterId("123")
                .courtFinderUrl("website url 1")
                .regionId("123")
                .epimmsId("epims-1")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build());
    }

    private List<BuildingLocation> getValidBuildingLocations() {
        return ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .area("Area ABCD")
                .clusterId("123")
                .courtFinderUrl("website url 1")
                .regionId("123")
                .epimmsId("epims1")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build(),
            BuildingLocation.builder()
                .buildingLocationName("building 2")
                .postcode("E1 23B")
                .address("Address ABCD")
                .area("Area ABCDE")
                .clusterId("123")
                .courtFinderUrl("website url 2")
                .regionId("123")
                .epimmsId("epims_2")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );
    }


    private List<BuildingLocation> getZeroByteBuildingLocations() {
        return ImmutableList.of(
            BuildingLocation.builder()
                .buildingLocationName("building 1")
                .postcode("E1 23A")
                .address("Address ABC")
                .area("Area \u200BABCD")
                .clusterId("123")
                .courtFinderUrl("website\u200B url 1")
                .regionId("123")
                .epimmsId("epims1")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Welsh  Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build(),
            BuildingLocation.builder()
                .buildingLocationName("building 2")
                .postcode("E1 23B")
                .address("Address ABCD")
                .area("Area ABCDE")
                .clusterId("123")
                .courtFinderUrl(" Bwebsite url 2")
                .regionId("123")
                .epimmsId("epims2")
                .buildingLocationStatus("OPEN")
                .welshBuildingLocationName("welsh building")
                .welshAddress("Wel\u200Bsh Address")
                .uprn("1234")
                .latitude(1111.2222)
                .longitude(3333.4444)
                .mrdBuildingLocationId("98765")
                .build()
        );
    }

}
