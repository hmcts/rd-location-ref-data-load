package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SafeExceptionProcessorTest {

    @Test
    void sanitizesSensitiveQueryParametersInExceptionMessages() {
        Exception exception = new RuntimeException(
            "Request failed: azure-storage-blob://account/archive/file.csv?sig=secret-signature&operation=read"
        );

        Exception sanitized = SafeExceptionProcessor.sanitize(exception);

        assertThat(sanitized.getMessage())
            .contains("sig=[REDACTED]")
            .doesNotContain("secret-signature")
            .contains("&operation=read");
    }

    @Test
    void sanitizesCredentialsAndSharedKeysInNestedExceptions() {
        Exception exception = new RuntimeException(
            "Blob read failed",
            new IllegalStateException(
                "credentials=account-secret;AccountKey=shared-secret"
            )
        );

        Exception sanitized = SafeExceptionProcessor.sanitize(exception);

        assertThat(sanitized.getCause().getMessage())
            .contains("credentials=[REDACTED]")
            .contains("AccountKey=[REDACTED]")
            .doesNotContain("account-secret", "shared-secret");
    }

}
