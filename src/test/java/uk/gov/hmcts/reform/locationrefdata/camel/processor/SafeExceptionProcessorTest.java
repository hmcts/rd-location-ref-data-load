package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.FileResponseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.FileStatus;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.service.IAuditService;

import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;
import static org.assertj.core.api.Assertions.assertThat;
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
        final SafeExceptionProcessor processor = new SafeExceptionProcessor();
        final CamelContext camelContext = new DefaultCamelContext();
        final Exchange exchange = new DefaultExchange(camelContext);
        final ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
        final ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        final IAuditService auditService = mock(IAuditService.class);
        final FileResponseProcessor fileResponseProcessor = mock(FileResponseProcessor.class);
        Exception exception = new RuntimeException("Request failed: ?token=secret-token");
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName("input.csv");
        routeProperties.setTableName("location");

        exchange.setProperty(EXCEPTION_CAUGHT, exception);
        exchange.getIn().setHeader("routedetails", routeProperties);
        camelContext.getRegistry().bind("input.csv", FileStatus.builder().fileName("input.csv").build());
        when(applicationContext.getBeanFactory()).thenReturn(beanFactory);
        ReflectionTestUtils.setField(processor, "camelContext", camelContext);
        ReflectionTestUtils.setField(processor, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(processor, "auditService", auditService);
        ReflectionTestUtils.setField(processor, "fileResponseProcessor", fileResponseProcessor);

        processor.process(exchange);

        Exception sanitized = exchange.getProperty(EXCEPTION_CAUGHT, Exception.class);
        assertThat(sanitized.getMessage())
            .contains("?token=[REDACTED]")
            .doesNotContain("secret-token");
        verify(fileResponseProcessor).process(exchange);
        verify(auditService).auditException(camelContext, "Request failed: ?token=[REDACTED]");
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
