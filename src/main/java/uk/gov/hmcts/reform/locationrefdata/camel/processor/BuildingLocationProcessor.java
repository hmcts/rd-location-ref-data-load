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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.ALPHANUMERIC_UNDERSCORE_REGEX;

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

        log.info(" {} Number of building locations before validation {}::",
                 logComponentName, buildingLocations.size()
        );

        List<BuildingLocation> validatedBuildingLocations = validate(
            buildingLocationJsrValidatorInitializer,
            buildingLocations
        );
        log.info(" {} Number of building locations after applying the JSR validator are {}::",
                 logComponentName, validatedBuildingLocations.size()
        );

        List<BuildingLocation> locationsWithValidEpimsId =
            validateEpimsIdInAllBuildingLocations(validatedBuildingLocations);
        log.info(" {} Number of building locations with valid epims id are {}::",
                 logComponentName, locationsWithValidEpimsId.size()
        );

        log.info(" {} {} Records Skipped due to an invalid epims id::",
                 logComponentName, buildingLocations.size() - locationsWithValidEpimsId.size());

        audit(buildingLocationJsrValidatorInitializer, exchange);

        if (validatedBuildingLocations.isEmpty()) {
            log.error(" {} No valid building location is found in the input file::", logComponentName);
            throw new RouteFailedException("No valid building locations found in the input file. "
                                               + "Please review and try again.");
        }

        exchange.getMessage().setBody(validatedBuildingLocations);
    }

    private List<BuildingLocation> validateEpimsIdInAllBuildingLocations(List<BuildingLocation> buildingLocations) {
        Predicate<BuildingLocation> isValidEpimsId =
            (location) -> isNotBlank(location.getEpimmsId())
                && Pattern.matches(ALPHANUMERIC_UNDERSCORE_REGEX, location.getEpimmsId());

        return buildingLocations.stream().filter(isValidEpimsId).collect(Collectors.toList());
    }
}
