package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
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
class CourtVenueProcessorTest {

    @Spy
    private CourtVenueProcessor processor = new CourtVenueProcessor();

    CamelContext camelContext = new DefaultCamelContext();

    Exchange exchange = new DefaultExchange(camelContext);

    JsrValidatorInitializer<CourtVenue> courtVenueJsrValidatorInitializer = new JsrValidatorInitializer<>();

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    PlatformTransactionManager platformTransactionManager;

    @Mock
    ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Mock
    ConfigurableApplicationContext applicationContext;

    private static final List<String> ZERO_BYTE_CHARACTERS = List.of("\u200B", " ");

    private static final List<Pair<String, Long>> ZERO_BYTE_CHARACTER_RECORDS = List.of(
        Pair.of("BFA1-001AD", null),
        Pair.of("BFA1-PAD", null),
        Pair.of("BFA1-DC\u200BX", null));

    DataQualityCheckConfiguration dataQualityCheckConfiguration = new DataQualityCheckConfiguration();

    @BeforeEach
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        setField(courtVenueJsrValidatorInitializer, "validator", validator);
        setField(processor, "courtVenueJsrValidatorInitializer", courtVenueJsrValidatorInitializer);
        setField(processor, "logComponentName", "testlogger");
        setField(processor, "regionQuery", "ids");
        setField(processor, "clusterQuery", "ids");
        setField(processor, "epimmsIdQuery", "ids");
        setField(processor, "courtTypeIdQuery", "ids");
        setField(courtVenueJsrValidatorInitializer, "camelContext", camelContext);
        setField(processor, "jdbcTemplate", jdbcTemplate);
        setField(courtVenueJsrValidatorInitializer, "jdbcTemplate", jdbcTemplate);
        setField(courtVenueJsrValidatorInitializer, "platformTransactionManager",
                 platformTransactionManager
        );
        setField(dataQualityCheckConfiguration, "zeroByteCharacters", ZERO_BYTE_CHARACTERS);
        setField(processor, "dataQualityCheckConfiguration", dataQualityCheckConfiguration);
        setField(processor, "applicationContext", applicationContext);
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("test");
        exchange.getIn().setHeader(ROUTE_DETAILS, routeProperties);
    }

    @Test
    void testCourtVenueCsv_0byte_characters() throws Exception {
        List<CourtVenue> courtVenues = getZeroDataCourtVenues();
        exchange.getIn().setBody(courtVenues);

        when(((ConfigurableApplicationContext)
            applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);

        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List actualLovServiceList = (List) exchange.getMessage().getBody();
        Assertions.assertEquals(5, actualLovServiceList.size());
        verify(courtVenueJsrValidatorInitializer, times(1))
            .auditJsrExceptions(eq(ZERO_BYTE_CHARACTER_RECORDS),
                                eq(null),
                                eq("Zero byte characters identified - check source file"),
                                eq(exchange));
    }


    @Test
    void testProcess() throws Exception {
        List<CourtVenue> expectedCourtVenues = getValidCourtVenues();

        exchange.getIn().setBody(expectedCourtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(expectedCourtVenues);
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.addAll(getInvalidCourtVenues());
        courtVenues.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .usingRecursiveComparison().isEqualTo(getValidCourtVenues());
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues_Invalid_Region_Cluster_EpimsId() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.add(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("abc")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build()
        );
        courtVenues.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());

        // testProcessWithValidAndInvalidCourtVenues_InvalidClusterId
        List<CourtVenue> courtVenues1 = new ArrayList<>();
        courtVenues1.add(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("123")
                .courtTypeId("2")
                .clusterId("abc")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build()
        );
        courtVenues1.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues1);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(2)).process(exchange);

        List<CourtVenue> actualCourtVenues1 = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues1)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());

        // testProcessWithValidAndInvalidCourtVenues_InvalidEpimmsId()

        List<CourtVenue> courtVenues2 = new ArrayList<>();
        courtVenues2.add(
            CourtVenue.builder()
                .epimmsId("epims123")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("123")
                .courtTypeId("2")
                .clusterId("1")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build()
        );
        courtVenues2.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues2);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        setJdbcTemplateResponse();
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(3)).process(exchange);

        List<CourtVenue> actualCourtVenues2 = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues2)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues_InvalidCourtTypeId() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.add(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("123")
                .courtTypeId("200000000")
                .clusterId("1")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build()
        );
        courtVenues.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    void testProcessWithInvalidCourtVenues() throws Exception {
        List<CourtVenue> courtVenues = getInvalidCourtVenues();

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }

    @Test
    void testProcessWithSingleInvalidCourtVenue_InvalidRegionId() throws Exception {
        List<CourtVenue> courtVenues = ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("abc")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }

    @Test
    void testProcessWithSingleInvalidCourtVenue_InvalidClusterId() throws Exception {
        List<CourtVenue> courtVenues = ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("abc")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }

    @Test
    void testProcessWithSingleInvalidCourtVenue_InvalidEpimmsId() throws Exception {
        List<CourtVenue> courtVenues = ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("abc")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("1")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }

    @Test
    void testProcessWithSingleInvalidCourtVenue_InvalidCourtTypeId() throws Exception {
        List<CourtVenue> courtVenues = ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("20000000")
                .clusterId("1")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .siteName("test site")
                .build());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        setJdbcTemplateResponse();
        assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }

    private List<CourtVenue> getInvalidCourtVenues() {
        return ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("@£$%128")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .build());
    }

    private List<CourtVenue> getValidCourtVenues() {
        return ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("1")
                .siteName("Test Site")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("Yes")
                .courtAddress("Test Court Address")
                .postcode("ABC 123")
                .phoneNumber("12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx Address")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .build(),
            CourtVenue.builder()
                .epimmsId("1")
                .siteName("Test Site1")
                .courtName("Test Court Name1")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("No")
                .courtAddress("Test Court Address1")
                .postcode("ABD 123")
                .phoneNumber("12343434")
                .closedDate("12/03/22")
                .courtLocationCode("12AC")
                .dxAddress("Test Dx Address1")
                .welshSiteName("Test Welsh Site Name1")
                .welshCourtAddress("Test Welsh Court Address1")
                .build()
        );
    }

    private List<CourtVenue> getZeroDataCourtVenues() {
        return ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("1")
                .siteName("Test \u200BSite")
                .courtName("Test Court Name")
                .courtStatus("\u200BOpen")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("Yes\u200B")
                .courtAddress("Test\u200B Court Address")
                .postcode("ABC \u200B123")
                .phoneNumber("\u200B12343434")
                .closedDate("12/03/21")
                .courtLocationCode("12AB")
                .dxAddress("Test Dx \u200BAddress")
                .welshSiteName("Test Welsh Site Name")
                .welshCourtAddress("Test Welsh Court Address")
                .build(),
            CourtVenue.builder()
                .epimmsId("1")
                .siteName("Test Site1")
                .courtName("Test \u200BCourt Name1")
                .courtStatus("\u200BOpen")
                .courtOpenDate("12/12/12")
                .regionId("1")
                .courtTypeId("2")
                .clusterId("3")
                .openForPublic("No")
                .courtAddress("Test\u200B Court Address1")
                .postcode("ABD 123")
                .phoneNumber("12343434")
                .closedDate("12/03/22")
                .courtLocationCode("12AC")
                .dxAddress("Test Dx Address1")
                .welshSiteName("Test\u200B Welsh Site Name1")
                .welshCourtAddress("Test Welsh Court Address1")
                .build()
        );
    }

    private void setJdbcTemplateResponse() {
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "2", "3"));
    }

}

