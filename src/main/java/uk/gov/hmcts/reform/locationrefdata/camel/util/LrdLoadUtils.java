package uk.gov.hmcts.reform.locationrefdata.camel.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

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
        boolean isPass = Boolean.TRUE;

        if (isNotEmpty(id)) {
            isPass = knownIdList.contains(id);
        }

        return !isPass;
    }

    public static  <T> List<T> filterDomainObjects(
        List<T> domainObjects,
        Predicate<T> predicate) {

        return domainObjects.stream()
            .filter(predicate).collect(Collectors.toList());
    }
}
