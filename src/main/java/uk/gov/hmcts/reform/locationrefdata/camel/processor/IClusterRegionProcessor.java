package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.FileStatus;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.validator.JsrValidatorInitializer;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.BuildingLocation;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LogDto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.camel.util.ObjectHelper.isNotEmpty;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil.getFileDetails;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.DataLoadUtil.registerFileStatusBean;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ROUTE_DETAILS;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.filterDomainObjects;

public interface IClusterRegionProcessor<T> {

    @Slf4j
    final class Logger {
    }

    default void handleListWithConstraintViolations(
        List<T> validatedDomains,
        List<T> objectsWithIntegrityViolations,
        Exchange exchange,
        String fieldName, String exceptionMessage,
        JsrValidatorInitializer<T> jsrValidatorInitializer) {

        Type mySuperclass = getType();
        validatedDomains.removeAll(objectsWithIntegrityViolations);

        List<Pair<String, Long>> invalidData = new ArrayList<>();

        if (isNotEmpty(objectsWithIntegrityViolations)) {

            if (((Class) mySuperclass).getCanonicalName().equals(BuildingLocation.class.getCanonicalName())) {
                objectsWithIntegrityViolations
                    .stream()
                    .map(BuildingLocation.class::cast)
                    .forEach(b -> invalidData.add(Pair.of(b.getEpimmsId(), b.getRowId())));
                jsrValidatorInitializer.auditJsrExceptions(invalidData,
                                                           fieldName, exceptionMessage, exchange);
            } else if (((Class) mySuperclass).getCanonicalName().equals(CourtVenue.class.getCanonicalName())) {
                objectsWithIntegrityViolations.stream()
                    .map(CourtVenue.class::cast)
                    .forEach(b -> invalidData.add(Pair.of(b.getEpimmsId(), b.getRowId())));

                jsrValidatorInitializer.auditJsrExceptions(invalidData,
                                                           fieldName, exceptionMessage, exchange);
            }
        }
    }

    default void setFileStatus(Exchange exchange, ApplicationContext applicationContext, String status) {
        RouteProperties routeProperties = (RouteProperties) exchange.getIn().getHeader(ROUTE_DETAILS);
        FileStatus fileStatus = getFileDetails(exchange.getContext(), routeProperties.getFileName());
        fileStatus.setAuditStatus(status);
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

    default void checkForeignKeyConstraint(List<T> validatedDomains,
                                   Predicate<T> predicate,
                                   String field, String exceptionMessage, LogDto logDto,
                                   Exchange exchange,
                                   JsrValidatorInitializer<T> jsrValidatorInitializer) {

        List<T> predicateCheckFailedLocations =
            filterDomainObjects(validatedDomains, predicate);
        Logger.log.info(logDto.getLogMessage(),
                 logDto.getLogComponentName(), validatedDomains.size() - predicateCheckFailedLocations.size());

        handleListWithConstraintViolations(validatedDomains, predicateCheckFailedLocations, exchange,
                                           field,
                                           exceptionMessage,
                                           jsrValidatorInitializer);
    }

    default List<String> getIdList(JdbcTemplate jdbcTemplate, String query) {
        return jdbcTemplate.queryForList(query, String.class);
    }

}
