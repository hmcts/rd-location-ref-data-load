package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.Exchange;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ExceptionProcessor;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;

@Component
@Primary
public class SafeExceptionProcessor extends ExceptionProcessor {

    private static final Pattern SENSITIVE_PARAMETER = Pattern.compile(
        "(?i)((?:^|[?&;])(?:sig|signature|sv|se|sp|sr|st|ss|srt|skoid|sktid|skt|ske|sks|skv|saoid|suoid|scid"
            + "|token|access_token|sharedkey|accountkey|sharedaccesssignature|credentials|key|secret)=)"
                + "[^&;\\s,)}\\]]+"
    );
    private static final Pattern SHARED_KEY = Pattern.compile(
        "(?i)(SharedKey\\s+[^:\\s]+:)[^\\s,)}]+"
    );

    @Override
    public void process(Exchange exchange) throws Exception {
        Object caught = exchange.getProperty(EXCEPTION_CAUGHT);
        if (caught instanceof Exception) {
            exchange.setProperty(EXCEPTION_CAUGHT, sanitize((Exception) caught));
        }
        super.process(exchange);
    }

    static Exception sanitize(Exception exception) {
        return sanitize(exception, Collections.newSetFromMap(new IdentityHashMap<>()), 0);
    }

    private static Exception sanitize(Exception exception, Set<Exception> seen, int depth) {
        if (exception == null || depth >= 10 || !seen.add(exception)) {
            return null;
        }

        Throwable originalCause = exception.getCause();
        Exception cause = originalCause instanceof Exception
            ? sanitize((Exception) originalCause, seen, depth + 1) : null;
        String message = exception.getMessage();
        String safeMessage = message == null ? exception.getClass().getSimpleName() : redact(message);
        Exception sanitized = new RuntimeException(safeMessage, cause);
        sanitized.setStackTrace(exception.getStackTrace());
        return sanitized;
    }

    private static String redact(String message) {
        return SHARED_KEY.matcher(SENSITIVE_PARAMETER.matcher(message).replaceAll("$1[REDACTED]"))
            .replaceAll("$1[REDACTED]");
    }
}
