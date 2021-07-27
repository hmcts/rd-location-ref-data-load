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
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.CLUSTER_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.COURT_TYPE_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.COURT_TYPE_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.EPIMMS_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.EPIMMS_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.REGION_ID_NOT_EXISTS;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.checkIfValueNotInListIfPresent;

@Slf4j
@Component
public class CourtVenueProcessor extends JsrValidationBaseProcessor<CourtVenue>
    implements IClusterRegionProcessor<CourtVenue> {

    @Autowired
    JsrValidatorInitializer<CourtVenue> courtVenueJsrValidatorInitializer;

    @Value("${logging-component-name}")
    private String logComponentName;

    @Value("${region-query}")
    private String regionQuery;

    @Value("${cluster-query}")
    private String clusterQuery;

    @Value("${epimms-id-query}")
    private String epimmsIdQuery;

    @Value("${court-type-id-query}")
    private String courtTypeIdQuery;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        int jsrValidatedCourtVenues = filteredCourtVenues.size();

        log.info(" {} Court Venue Records count after Validation {}::",
                 logComponentName, jsrValidatedCourtVenues
        );

        filterCourtVenuesForForeignKeyViolations(filteredCourtVenues, exchange);

        audit(courtVenueJsrValidatorInitializer, exchange);

        if (filteredCourtVenues.isEmpty()) {
            log.error(" {} Court Venue upload failed as no valid records present::", logComponentName);
            throw new RouteFailedException("Court Venue upload failed as no valid records present");
        }

        if (filteredCourtVenues.size() != jsrValidatedCourtVenues) {
            setFileStatus(exchange, applicationContext);
        }

        exchange.getMessage().setBody(filteredCourtVenues);
    }

    @SuppressWarnings("unchecked")
    private void filterCourtVenuesForForeignKeyViolations(List<CourtVenue> validatedCourtVenues,
                                                          Exchange exchange) {

        if (isNotEmpty(validatedCourtVenues)) {
            var epimmsIdList = getIdList(jdbcTemplate, epimmsIdQuery);
            checkForeignKeyConstraint(
                validatedCourtVenues,
                location -> checkIfValueNotInListIfPresent(location.getEpimmsId(), epimmsIdList),
                EPIMMS_ID, EPIMMS_ID_NOT_EXISTS,
                "{}:: Number of valid court venues after applying the epimms Id check filter: {}",
                exchange, logComponentName, courtVenueJsrValidatorInitializer
            );
            if (isNotEmpty(validatedCourtVenues)) {
                var courtTypeIdList = getIdList(jdbcTemplate, courtTypeIdQuery);
                checkForeignKeyConstraint(
                    validatedCourtVenues,
                    location -> checkIfValueNotInListIfPresent(location.getCourtTypeId(), courtTypeIdList),
                    COURT_TYPE_ID, COURT_TYPE_ID_NOT_EXISTS,
                    "{}:: Number of valid court venues after applying the court type id check filter: {}",
                    exchange, logComponentName, courtVenueJsrValidatorInitializer
                );
                if (isNotEmpty(validatedCourtVenues)) {
                    var regionIdList = getIdList(jdbcTemplate, regionQuery);
                    checkForeignKeyConstraint(
                        validatedCourtVenues,
                        location -> checkIfValueNotInListIfPresent(location.getRegionId(), regionIdList),
                        REGION_ID, REGION_ID_NOT_EXISTS,
                        "{}:: Number of valid court venues after applying the region check filter: {}",
                        exchange, logComponentName, courtVenueJsrValidatorInitializer
                    );
                    if (isNotEmpty(validatedCourtVenues)) {
                        var clusterIdList = getIdList(jdbcTemplate, clusterQuery);
                        checkForeignKeyConstraint(
                            validatedCourtVenues,
                            location -> checkIfValueNotInListIfPresent(location.getClusterId(), clusterIdList),
                            CLUSTER_ID, CLUSTER_ID_NOT_EXISTS,
                            "{}:: Number of valid court venues after applying the cluster check filter: {}",
                            exchange, logComponentName, courtVenueJsrValidatorInitializer
                        );
                    }
                }
            }
        }
    }
}
