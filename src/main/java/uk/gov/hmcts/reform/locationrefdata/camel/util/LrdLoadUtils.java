package uk.gov.hmcts.reform.locationrefdata.camel.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.DATE_TIME_FORMAT;

public class LrdLoadUtils {

    private LrdLoadUtils() {

    }

    public static String trim(String value) {
        return nonNull(value) ? value.trim() : null;
    }

    public static String trimNumeric(String value) {
        return isNotBlank(value) ? value.trim() : null;
    }

    public static boolean checkIfValueNotInListIfPresent(String id, List<String> knownIdList) {
        return (isNotEmpty(id)) ? isFalse(knownIdList.contains(id)) : Boolean.FALSE;
    }

    public static <T> List<T> filterDomainObjects(List<T> domainObjects, Predicate<T> predicate) {
        return domainObjects.stream()
            .filter(predicate).toList();
    }

    public static Timestamp getDateTimeStamp(String dateTime) {
        if (!StringUtils.isBlank(dateTime)) {
            LocalDateTime ldt = LocalDateTime.parse(dateTime,
                                                    DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
            return Timestamp.valueOf(ldt);
        }
        return null;
    }
}
