package uk.gov.hmcts.reform.locationrefdata.cameltest;

import com.azure.storage.common.StorageSharedKeyCredential;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ExceptionProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.FileResponseProcessor;
import uk.gov.hmcts.reform.data.ingestion.camel.route.beans.RouteProperties;
import uk.gov.hmcts.reform.data.ingestion.camel.service.IAuditService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.ROUTE_DETAILS;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_NAME;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;
import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.TABLE_NAME;

@ExtendWith(OutputCaptureExtension.class)
class BlobCredentialLoggingTest extends CamelTestSupport {

    private static final String ACCOUNT_NAME = "rdpreview";
    private static final String FILE_NAME = "credential-log-probe-" + UUID.randomUUID();
    private static final String CONTAINER_NAME = "lrd-ref-data";
    private static final String TABLE_NAME_VALUE = "credential_log_probe";
    private static final String SCHEDULER_NAME_VALUE = "credential-log-probe";

    private IAuditService auditService;
    private ExceptionProcessor exceptionProcessor;
    private String accountKey;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        String accountName = setting("BLOB_ACCOUNT_NAME", "azure.storage.account-name");
        accountKey = setting("BLOB_ACCOUNT_KEY", "azure.storage.account-key");
        assumeTrue(accountName != null && !accountName.isBlank(), "Set BLOB_ACCOUNT_NAME to run this Azure test");
        assumeTrue(accountKey != null && !accountKey.isBlank(), "Set BLOB_ACCOUNT_KEY to run this Azure test");
        assertEquals(ACCOUNT_NAME, accountName, "This read-only probe is restricted to the rdpreview account");

        SimpleRegistry registry = new SimpleRegistry();
        registry.bind("credsreg", new StorageSharedKeyCredential(accountName, accountKey));
        CamelContext context = new DefaultCamelContext(registry);

        context.getGlobalOptions().put(SCHEDULER_START_TIME, String.valueOf(System.currentTimeMillis()));
        context.getGlobalOptions().put(SCHEDULER_NAME, SCHEDULER_NAME_VALUE);
        context.getGlobalOptions().put(TABLE_NAME, TABLE_NAME_VALUE);

        auditService = mock(IAuditService.class);
        exceptionProcessor = new ExceptionProcessor();
        setField(exceptionProcessor, "camelContext", context);
        setField(exceptionProcessor, "applicationContext", applicationContext());
        setField(exceptionProcessor, "auditService", auditService);
        setField(exceptionProcessor, "fileResponseProcessor", mock(FileResponseProcessor.class));
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        RouteProperties routeProperties = new RouteProperties();
        routeProperties.setFileName(FILE_NAME);
        routeProperties.setTableName(TABLE_NAME_VALUE);
        String endpoint = "azure-storage-blob://" + ACCOUNT_NAME + "/" + CONTAINER_NAME
            + "?credentials=#credsreg&operation=getBlob&blobName=" + FILE_NAME;

        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Exception.class)
                    .handled(true)
                    .process(exceptionProcessor);

                from("direct:credential-log-probe")
                    .setHeader(ROUTE_DETAILS, constant(routeProperties))
                    .to(endpoint);
            }
        };
    }

    @Test
    void realBlobRouteFailureDoesNotLogOrAuditStorageKey(CapturedOutput output) {
        template.sendBody("direct:credential-log-probe", null);

        String logs = output.getAll();
        assertTrue(
            logs.contains("Status code 403") || logs.contains("Status code 404"),
            "Expected Azure to fail the blob request with HTTP 403 or 404"
        );
        assertFalse(logs.contains(accountKey), "Storage key appeared in the captured route logs");
        verify(auditService).auditException(eq(context), org.mockito.ArgumentMatchers.argThat(
            description -> description != null && !description.contains(accountKey)
        ));
    }

    private static ConfigurableApplicationContext applicationContext() {
        ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(applicationContext.getBeanFactory()).thenReturn(beanFactory);
        return applicationContext;
    }

    private static String setting(String environmentName, String propertyName) {
        String environmentValue = System.getenv(environmentName);
        return environmentValue == null ? System.getProperty(propertyName) : environmentValue;
    }
}
