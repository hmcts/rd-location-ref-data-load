package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.Registry;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.FileResponseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.FileStatus;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.service.IAuditService;

import java.util.HashMap;

import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void processesExceptionWithSanitizedMessage() throws Exception {
        SafeExceptionProcessor processor = new SafeExceptionProcessor();
        Exchange exchange = mock(Exchange.class);
        CamelContext camelContext = mock(CamelContext.class);
        Registry registry = mock(Registry.class);
        Message message = mock(Message.class);
        IAuditService auditService = mock(IAuditService.class);
        FileResponseProcessor fileResponseProcessor = mock(FileResponseProcessor.class);
        Exception exception = new RuntimeException("Request failed: token=secret-token");
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("input.csv");
        routeProperties.setTableName("location");

        when(exchange.getProperty(EXCEPTION_CAUGHT)).thenReturn(exception);
        when(exchange.getContext()).thenReturn(camelContext);
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader("routedetails")).thenReturn(routeProperties);
        when(camelContext.getGlobalOptions()).thenReturn(new HashMap<>());
        when(camelContext.getRegistry()).thenReturn(registry);
        when(registry.lookupByName("input.csv")).thenReturn(
            FileStatus.builder().fileName("input.csv").build()
        );
        ReflectionTestUtils.setField(processor, "camelContext", camelContext);
        ReflectionTestUtils.setField(processor, "applicationContext", mock(ApplicationContext.class));
        ReflectionTestUtils.setField(processor, "auditService", auditService);
        ReflectionTestUtils.setField(processor, "fileResponseProcessor", fileResponseProcessor);

        processor.process(exchange);

        verify(exchange).setProperty(
            EXCEPTION_CAUGHT,
            argThat(
                caught -> caught instanceof Exception sanitized
                    && sanitized.getMessage().contains("token=[REDACTED]")
                    && !sanitized.getMessage().contains("secret-token")
            )
        );
        verify(fileResponseProcessor).process(exchange);
    }

    @Test
    void stopsSanitizingCauseChainAtMaximumDepth() {
        Exception exception = new RuntimeException("level 0");
        Exception current = exception;
        for (int depth = 1; depth <= 10; depth++) {
            Exception cause = new RuntimeException("level " + depth);
            current.initCause(cause);
            current = cause;
        }

        Throwable sanitized = SafeExceptionProcessor.sanitize(exception);

        int sanitizedDepth = 0;
        while (sanitized != null) {
            sanitizedDepth++;
            sanitized = sanitized.getCause();
        }
        assertThat(sanitizedDepth).isEqualTo(10);
        assertThat(SafeExceptionProcessor.sanitize(null)).isNull();
    }

    @Test
    void stopsSanitizingCircularCauseChain() {
        Exception exception = new Exception("circular") {
            @Override
            public synchronized Throwable getCause() {
                return this;
            }
        };

        Exception sanitized = SafeExceptionProcessor.sanitize(exception);

        assertThat(sanitized.getCause()).isNull();
    }

}
