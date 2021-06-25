package uk.gov.hmcts.reform.locationrefdata.camel.util;

import static java.util.Objects.nonNull;

public class LrdLoadUtils {

    private LrdLoadUtils() {

    }

    public static String trim(String value) {
        return nonNull(value) ? value.trim() : null;
    }
}
