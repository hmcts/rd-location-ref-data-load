package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class CourtVenueProcessorTest {

    @Spy
    private CourtVenueProcessor processor = new CourtVenueProcessor();

    CamelContext camelContext = new DefaultCamelContext();

    Exchange exchange = new DefaultExchange(camelContext);

    JsrValidatorInitializer<CourtVenue> courtVenueJsrValidatorInitializer = new JsrValidatorInitializer<>();

    @BeforeEach
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        setField(courtVenueJsrValidatorInitializer, "validator", validator);
        setField(processor, "courtVenueJsrValidatorInitializer", courtVenueJsrValidatorInitializer);
        setField(processor, "logComponentName", "testlogger");
    }

    @Test
    public void testProcess() throws Exception {
        List<CourtVenue> expectedCourtVenues = getValidCourtVenues();

        exchange.getIn().setBody(expectedCourtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(expectedCourtVenues);
    }


    @Test
    public void testProcessWithValidAndInvalidCourtVenues() throws Exception {
        List<CourtVenue> courtVenues = new ArrayList<>();
        courtVenues.addAll(getInvalidCourtVenues());
        courtVenues.addAll(getValidCourtVenues());

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        processor.process(exchange);
        verify(processor, times(1)).process(exchange);

        List<CourtVenue> actualCourtVenues = (List<CourtVenue>) exchange.getMessage().getBody();

        assertThat(actualCourtVenues)
            .hasSize(2)
            .hasSameElementsAs(getValidCourtVenues());
    }

    @Test
    public void testProcessWithInvalidCourtVenues() throws Exception {
        List<CourtVenue> courtVenues = getInvalidCourtVenues();

        exchange.getIn().setBody(courtVenues);
        doNothing().when(processor).audit(courtVenueJsrValidatorInitializer, exchange);
        Assert.assertThrows(RouteFailedException.class, () -> processor.process(exchange));

        verify(processor, times(1)).process(exchange);
    }


    private List<CourtVenue> getInvalidCourtVenues() {
        return ImmutableList.of(
            CourtVenue.builder()
                .epimmsId("@£$%128")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId(1)
                .courtTypeId(2)
                .clusterId(3)
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
                .epimmsId("123456")
                .siteName("Test Site")
                .courtName("Test Court Name")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId(1)
                .courtTypeId(2)
                .clusterId(3)
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
                .epimmsId("123456")
                .siteName("Test Site1")
                .courtName("Test Court Name1")
                .courtStatus("Open")
                .courtOpenDate("12/12/12")
                .regionId(1)
                .courtTypeId(2)
                .clusterId(3)
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

