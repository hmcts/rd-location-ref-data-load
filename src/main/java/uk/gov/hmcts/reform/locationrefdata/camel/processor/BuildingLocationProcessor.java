package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LogDto;
import uk.gov.hmcts.reform.locationrefdata.configuration.DataQualityCheckConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.FAILURE;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.PARTIAL_SUCCESS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.checkIfValueNotInListIfPresent;

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

    public static final String ZERO_BYTE_CHARACTER_ERROR_MESSAGE =
        "Zero byte characters identified - check source file";

    @Autowired
    DataQualityCheckConfiguration dataQualityCheckConfiguration;


    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {

        var buildingLocations = exchange.getIn().getBody() instanceof List
            ? (List<BuildingLocation>) exchange.getIn().getBody()
            : singletonList((BuildingLocation) exchange.getIn().getBody());

        log.info("{}:: Number of building locations before validation {}::",
                 logComponentName, buildingLocations.size()
        );

        var validatedBuildingLocations = validate(
            buildingLocationJsrValidatorInitializer,
            buildingLocations
        );

        var jsrValidatedBuildingLocations = validatedBuildingLocations.size();
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
            setFileStatus(exchange, applicationContext,PARTIAL_SUCCESS);
        }

        if (buildingLocations != null && !buildingLocations.isEmpty()) {
            processExceptionRecords(exchange, buildingLocations);
        }

        exchange.getMessage().setBody(validatedBuildingLocations);

    }


    private void processExceptionRecords(Exchange exchange,
                                         List<BuildingLocation> buildingLocationsList) {
        List<Pair<String, Long>> zeroByteCharacterRecords = new ArrayList<>();
        buildingLocationsList.forEach(buildingLoc -> dataQualityCheckConfiguration.zeroByteCharacters
            .forEach(zeroByteChar -> {
                if (buildingLoc.toString().contains(zeroByteChar)) {
                    zeroByteCharacterRecords.add(Pair.of(
                        buildingLoc.getEpimmsId() + "::" + buildingLoc.getBuildingLocationName(),
                        buildingLoc.getRowId()
                    ));
                }
            }));
        List<Pair<String, Long>> distinctZeroByteCharacterRecords = zeroByteCharacterRecords.stream()
            .distinct().collect(Collectors.toList());
        if (!distinctZeroByteCharacterRecords.isEmpty()) {
            setFileStatus(exchange, applicationContext,FAILURE);

            buildingLocationJsrValidatorInitializer.auditJsrExceptions(distinctZeroByteCharacterRecords,null,
                                                                 ZERO_BYTE_CHARACTER_ERROR_MESSAGE,exchange);
        }
    }


    @SuppressWarnings("unchecked")
    private void filterBuildingLocationsForForeignKeyViolations(List<BuildingLocation> validatedBuildingLocations,
                                                                Exchange exchange) {

        if (isNotEmpty(validatedBuildingLocations)) {
            var regionIdList = getIdList(jdbcTemplate, regionQuery);
            checkForeignKeyConstraint(
                validatedBuildingLocations,
                location -> checkIfValueNotInListIfPresent(location.getRegionId(), regionIdList),
                REGION_ID, REGION_ID_NOT_EXISTS,
                new LogDto(
                    "{}:: Number of valid building locations after applying the region check filter: {}",
                           logComponentName),
                exchange, buildingLocationJsrValidatorInitializer
            );

            if (isNotEmpty(validatedBuildingLocations)) {
                var clusterIdList = getIdList(jdbcTemplate, clusterQuery);
                checkForeignKeyConstraint(
                    validatedBuildingLocations,
                    location -> checkIfValueNotInListIfPresent(location.getClusterId(), clusterIdList),
                    CLUSTER_ID, CLUSTER_ID_NOT_EXISTS,
                    new LogDto(
                    "{}:: Number of valid building locations after applying the cluster check filter: {}",
                    logComponentName),
                    exchange, buildingLocationJsrValidatorInitializer
                );
            }
        }
    }
}
