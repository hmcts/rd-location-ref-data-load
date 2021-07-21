package uk.gov.hmcts.reform.locationrefdata.camel.util;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.checkIfValueNotInListIfPresent;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trimNumeric;

public class LrdLoadUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {" abc", "abc ", " abc ", "abc"})
    public void testTrim(String value) {
        assertThat(trim(value))
            .isNotBlank()
            .isEqualTo("abc");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    public void testTrim_EmptyValuesPassed(String value) {
        assertThat(trim(value))
            .isNotNull()
            .isEqualTo("");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    public void testTrimNumeric_EmptyValuesPassed(String value) {
        assertThat(trimNumeric(value))
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {" abc", "abc ", " abc ", "abc"})
    public void testTrimNumeric(String value) {
        assertThat(trimNumeric(value))
            .isNotBlank()
            .isEqualTo("abc");
    }

    @Test
    public void testTrim_NullValuePassed() {
        assertThat(trim(null))
            .isNull();
    }

    @Test
    public void testTrimNumeric_NullValuePassed() {
        assertThat(trimNumeric(null))
            .isNull();
    }

    @Test
    void testCheckIfValueNotInListIfPresent() {
        assertThat(checkIfValueNotInListIfPresent("123", ImmutableList.of("123"))).isFalse();
    }

    @Test
    void testCheckIfValueNotInListIfPresent_NullIdPassed() {
        assertThat(checkIfValueNotInListIfPresent(null, ImmutableList.of("123"))).isFalse();
    }

    @Test
    void testCheckIfValueNotInListIfPresent_IdNotInList() {
        assertThat(checkIfValueNotInListIfPresent("abc", ImmutableList.of("123"))).isTrue();
    }

}
