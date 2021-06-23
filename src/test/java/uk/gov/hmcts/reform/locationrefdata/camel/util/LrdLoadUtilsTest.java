package uk.gov.hmcts.reform.locationrefdata.camel.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class LrdLoadUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {" abc", "abc ", " abc "})
    public void testTrim(String value) {
        assertThat(LrdLoadUtils.trim(value))
            .isNotBlank()
            .isEqualTo("abc");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    public void testTrim_EmptyValuesPassed(String value) {
        assertThat(LrdLoadUtils.trim(value))
            .isNotNull()
            .isEqualTo("");
    }

    @Test
    public void testTrim_NullValuePassed() {
        assertThat(LrdLoadUtils.trim(null))
            .isNull();
    }

}
