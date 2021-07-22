package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
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

import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ROUTE_DETAILS;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class CourtVenueProcessorTest {

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
        setField(courtVenueJsrValidatorInitializer, "camelContext", camelContext);
        setField(processor, "jdbcTemplate", jdbcTemplate);
        setField(courtVenueJsrValidatorInitializer, "jdbcTemplate", jdbcTemplate);
        setField(courtVenueJsrValidatorInitializer, "platformTransactionManager",
                 platformTransactionManager);
        setField(processor, "applicationContext", applicationContext);
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("test");
        exchange.getIn().setHeader(ROUTE_DETAILS, routeProperties);
    }

    @Test
    void testProcess() throws Exception {
        List<CourtVenue> expectedCourtVenues = getValidCourtVenues();

        exchange.getIn().setBody(expectedCourtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues_InvalidRegionId() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.add(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("abc")
                .courtTypeId(2)
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues_InvalidClusterId() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.add(
            CourtVenue.builder()
                .epimmsId("1")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("123")
                .courtTypeId(2)
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
        courtVenues.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
        when((applicationContext).getBeanFactory()).thenReturn(configurableListableBeanFactory);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    void testProcessWithValidAndInvalidCourtVenues_InvalidEpimmsId() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.add(
            CourtVenue.builder()
                .epimmsId("epims123")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId("123")
                .courtTypeId(2)
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
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
                .courtTypeId(2)
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
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
                .courtTypeId(2)
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
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
                .courtTypeId(2)
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
        when(jdbcTemplate.queryForList("ids", String.class)).thenReturn(ImmutableList.of("1", "3"));
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
                .courtTypeId(2)
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
                .courtTypeId(2)
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
                .courtTypeId(2)
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

}

