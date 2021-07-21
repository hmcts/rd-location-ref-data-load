package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.checkIfValueNotInListIfPresent;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.filterDomainObjects;

@Component
@Slf4j
public class BuildingLocationProcessor extends JsrValidationBaseProcessor<BuildingLocation>
    implements IClusterRegionProcessor<BuildingLocation> {

    @Autowired
    JsrValidatorInitializer<BuildingLocation> buildingLocationJsrValidatorInitializer;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${region-query}")
    private String regionQuery;

    @Value("${cluster-query}")
    private String clusterQuery;

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

        int jsrValidatedBuildingLocations = validatedBuildingLocations.size();
        log.info("{}:: Number of building locations after applying the JSR validator are {}::",
                 logComponentName, jsrValidatedBuildingLocations
        );

        filterBuildingLocationsForForeignKeyViolations(validatedBuildingLocations, exchange);

        audit(buildingLocationJsrValidatorInitializer, exchange);

        if (validatedBuildingLocations.isEmpty()) {
            log.error("{}:: No valid building location is found in the input file::", logComponentName);
            throw new RouteFailedException("No valid building locations found in the input file. "
                                               + "Please review and try again.");
        }

        if (validatedBuildingLocations.size() != jsrValidatedBuildingLocations) {
            setFileStatus(exchange, applicationContext);
        }

        exchange.getMessage().setBody(validatedBuildingLocations);
    }

    @SuppressWarnings("unchecked")
    private void filterBuildingLocationsForForeignKeyViolations(List<BuildingLocation> validatedBuildingLocations,
                                                                Exchange exchange) {

        if (isNotEmpty(validatedBuildingLocations.size())) {
            List<String> regionIds = jdbcTemplate.queryForList(regionQuery, String.class);
            Predicate<BuildingLocation> regionCheck =
                location -> checkIfValueNotInListIfPresent(location.getRegionId(), regionIds);
            List<BuildingLocation> regionCheckFailedLocations =
                filterDomainObjects(validatedBuildingLocations, regionCheck);

            log.info("{}:: Number of valid building locations after applying the region check filter: {}",
                     logComponentName, validatedBuildingLocations.size() - regionCheckFailedLocations.size());

            handleListWithConstraintViolations(validatedBuildingLocations, regionCheckFailedLocations, exchange,
                                               REGION_ID,
                                               REGION_ID_NOT_EXISTS,
                                               buildingLocationJsrValidatorInitializer);

            if (isNotEmpty(validatedBuildingLocations.size())) {
                List<String> clusterIds = jdbcTemplate.queryForList(clusterQuery, String.class);

                Predicate<BuildingLocation> clusterCheck =
                    location -> checkIfValueNotInListIfPresent(location.getClusterId(), clusterIds);
                List<BuildingLocation> clusterCheckFailedLocations =
                    filterDomainObjects(validatedBuildingLocations, clusterCheck);

                log.info("{}:: Number of valid building locations after applying the cluster check filter: {}",
                         logComponentName, validatedBuildingLocations.size() - clusterCheckFailedLocations.size());

                handleListWithConstraintViolations(validatedBuildingLocations, clusterCheckFailedLocations, exchange,
                                                   CLUSTER_ID,
                                                   CLUSTER_ID_NOT_EXISTS,
                                                   buildingLocationJsrValidatorInitializer
                );
            }
        }
    }
}
