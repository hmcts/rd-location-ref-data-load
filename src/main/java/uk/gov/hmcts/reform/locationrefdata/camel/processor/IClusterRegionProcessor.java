package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.Exchange;
import org.springframework.context.ApplicationContext;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.FileStatus;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.camel.util.ObjectHelper.isNotEmpty;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil.getFileDetails;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil.registerFileStatusBean;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.PARTIAL_SUCCESS;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ROUTE_DETAILS;

public interface IClusterRegionProcessor<T> {

    default void handleListWithConstraintViolations(
        List<T> validatedDomains,
        List<T> objectsWithIntegrityViolations,
        Exchange exchange,
        String fieldName, String exceptionMessage,
        JsrValidatorInitializer<T> jsrValidatorInitializer) {

        Type mySuperclass = getType();
        validatedDomains.removeAll(objectsWithIntegrityViolations);

        if (isNotEmpty(objectsWithIntegrityViolations)) {

            if (((Class) mySuperclass).getCanonicalName().equals(BuildingLocation.class.getCanonicalName())) {
                List<String> conditionFailedLocationIds = objectsWithIntegrityViolations
                                                            .stream()
                                                            .map(loc -> ((BuildingLocation) loc).getEpimmsId())
                                                            .collect(Collectors.toList());

                jsrValidatorInitializer.auditJsrExceptions(conditionFailedLocationIds,
                                                           fieldName, exceptionMessage, exchange);
            } else if (((Class) mySuperclass).getCanonicalName().equals(CourtVenue.class.getCanonicalName())) {
                List<String> conditionFailedCourtVenues = objectsWithIntegrityViolations.stream()
                                                            .map(s -> ((CourtVenue) s).getEpimmsId())
                                                            .collect(toList());

                jsrValidatorInitializer.auditJsrExceptions(conditionFailedCourtVenues,
                                                           fieldName, exceptionMessage, exchange);
            }
        }
    }

    default void setFileStatus(Exchange exchange, ApplicationContext applicationContext) {
        RouteProperties routeProperties = (RouteProperties) exchange.getIn().getHeader(ROUTE_DETAILS);
        FileStatus fileStatus = getFileDetails(exchange.getContext(), routeProperties.getFileName());
        fileStatus.setAuditStatus(PARTIAL_SUCCESS);
        registerFileStatusBean(applicationContext, routeProperties.getFileName(), fileStatus,
                               exchange.getContext());
    }

    private Type getType() {
        Type genericSuperClass = getClass().getGenericSuperclass();
        ParameterizedType parametrizedType = null;
        while (parametrizedType == null) {
            if ((genericSuperClass instanceof ParameterizedType)) {
                parametrizedType = (ParameterizedType) genericSuperClass;
            } else {
                genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
            }
        }

        return parametrizedType.getActualTypeArguments()[0];

    }

}
