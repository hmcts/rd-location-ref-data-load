package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
@Slf4j
public class BuildingLocationProcessor extends JsrValidationBaseProcessor<BuildingLocation> {

    @Autowired
    JsrValidatorInitializer<BuildingLocation> buildingLocationJsrValidatorInitializer;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {

        List<BuildingLocation> buildingLocations = exchange.getIn().getBody() instanceof List
            ? (List<BuildingLocation>) exchange.getIn().getBody()
            : singletonList((BuildingLocation) exchange.getIn().getBody());

        log.info("{}:: Number of building locations before validation {}::",
                 logComponentName, buildingLocations.size()
        );

        List<BuildingLocation> validatedBuildingLocations = validate(
            buildingLocationJsrValidatorInitializer,
            buildingLocations
        );
        log.info("{}:: Number of building locations after applying the JSR validator are {}::",
                 logComponentName, validatedBuildingLocations.size()
        );

        audit(buildingLocationJsrValidatorInitializer, exchange);

        if (validatedBuildingLocations.isEmpty()) {
            log.error("{}:: No valid building location is found in the input file::", logComponentName);
            throw new RouteFailedException("No valid building locations found in the input file. "
                                               + "Please review and try again.");
        }

        exchange.getMessage().setBody(validatedBuildingLocations);
    }

}
