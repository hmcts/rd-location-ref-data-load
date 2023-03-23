package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.exception.RouteFailedException;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.JsrValidationBaseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.ServiceToCcdCaseType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.subtractExact;
import static java.util.Collections.singletonList;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Slf4j
@Component
public class ServiceToCcdCaseTypeProcessor extends JsrValidationBaseProcessor<ServiceToCcdCaseType> {

    @Autowired
    JsrValidatorInitializer<ServiceToCcdCaseType> serviceToCcdServiceJsrValidatorInitializer;

    @Value("${logging-component-name}")
    private String logComponentName;


    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {

        List<ServiceToCcdCaseType> serviceToCcdCaseTypes;

        serviceToCcdCaseTypes = exchange.getIn().getBody() instanceof List
            ? (List<ServiceToCcdCaseType>) exchange.getIn().getBody()
            : singletonList((ServiceToCcdCaseType) exchange.getIn().getBody());

        log.info(" {} ServiceToCCDService Records count before Validation & before merging service names {}::",
                 logComponentName, serviceToCcdCaseTypes.size()
        );

        var refinedServiceToCcdCaseTypes = populateService(serviceToCcdCaseTypes);
        log.info(" {} ServiceToCCDService Records count before Validation & after merging service names {}::",
                 logComponentName, refinedServiceToCcdCaseTypes.size()
        );

        log.info(" {} {} Records Skipped due to blank service name and Case types::", logComponentName,
                 subtractExact(serviceToCcdCaseTypes.size(), refinedServiceToCcdCaseTypes.size()));

        List<ServiceToCcdCaseType> filteredServiceToCcdCaseTypes = validate(
            serviceToCcdServiceJsrValidatorInitializer,
            refinedServiceToCcdCaseTypes
        );
        log.info(" {} ServiceToCCDService Records count after Validation & after merging service names {}::",
                 logComponentName, filteredServiceToCcdCaseTypes.size()
        );

        audit(serviceToCcdServiceJsrValidatorInitializer, exchange);

        if (filteredServiceToCcdCaseTypes.isEmpty()) {
            log.error(" {} ServiceToCcdService failed as no valid records present::", logComponentName);
            throw new RouteFailedException("ServiceToCcdService failed as no valid records present");
        }

        exchange.getMessage().setBody(filteredServiceToCcdCaseTypes);
    }

    /**
     * Spits list by , serviceNames and recreates the list.
     *
     * @param serviceToCcdCaseTypes serviceToCcdServices
     * @return List
     */
    private List<ServiceToCcdCaseType> populateService(List<ServiceToCcdCaseType> serviceToCcdCaseTypes) {

        Predicate<ServiceToCcdCaseType> isValidCaseType = e -> isNotBlank(e.getCcdCaseType());
        Predicate<ServiceToCcdCaseType> isValidService = e -> isNotBlank(e.getCcdServiceName())
            && isEmpty(e.getCcdCaseType());

        List<ServiceToCcdCaseType> refinedServiceToCcdCaseTypes = new ArrayList<>();
        serviceToCcdCaseTypes.stream()
            .filter(isValidCaseType)
            .forEach(serviceToCcdService ->
                         of(serviceToCcdService.getCcdCaseType().split(","))
                             .forEach(caseTypes -> {
                                 ServiceToCcdCaseType serviceToCcdCaseType = new ServiceToCcdCaseType();
                                 serviceToCcdCaseType.setServiceCode(serviceToCcdService
                                                                                  .getServiceCode());
                                 serviceToCcdCaseType.setCcdServiceName(serviceToCcdService
                                                                                     .getCcdServiceName());
                                 serviceToCcdCaseType.setCcdCaseType(caseTypes);
                                 serviceToCcdCaseType.setRowId(serviceToCcdService.getRowId());
                                 refinedServiceToCcdCaseTypes.add(serviceToCcdCaseType);
                             }
            ));

        refinedServiceToCcdCaseTypes.addAll(serviceToCcdCaseTypes.stream()
                                                .filter(isValidService).toList());

        return refinedServiceToCcdCaseTypes;
    }
}
