package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.List;

import static java.util.Collections.singletonList;

@Slf4j
@Component
public class CourtVenueProcessor extends JsrValidationBaseProcessor<CourtVenue> {

    @Autowired
    JsrValidatorInitializer<CourtVenue> courtVenueJsrValidatorInitializer;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {

        List<CourtVenue> courtVenues;

        courtVenues = exchange.getIn().getBody() instanceof List
            ? (List<CourtVenue>) exchange.getIn().getBody()
            : singletonList((CourtVenue) exchange.getIn().getBody());

        log.info(" {} Court Venue Records count before Validation {}::",
                 logComponentName, courtVenues.size()
        );

        List<CourtVenue> filteredCourtVenues = validate(
            courtVenueJsrValidatorInitializer,
            courtVenues
        );
        log.info(" {} Court Venue Records count after Validation {}::",
                 logComponentName, filteredCourtVenues.size()
        );

        audit(courtVenueJsrValidatorInitializer, exchange);

        if (filteredCourtVenues.isEmpty()) {
            log.error(" {} Court Venue upload failed as no valid records present::", logComponentName);
            throw new RouteFailedException("Court Venue upload failed as no valid records present");
        }

        exchange.getMessage().setBody(filteredCourtVenues);
    }

}
