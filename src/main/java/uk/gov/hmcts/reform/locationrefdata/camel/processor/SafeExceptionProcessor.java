package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.Exchange;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.processor.ExceptionProcessor;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;

@Component
@Primary
public class SafeExceptionProcessor extends ExceptionProcessor {

    private static final Set<String> SENSITIVE_PARAMETER_NAMES = Set.of(
        "sig", "signature", "sv", "se", "sp", "sr", "st", "ss", "srt", "skoid", "sktid", "skt", "ske",
        "sks", "skv", "saoid", "suoid", "scid", "token", "access_token", "sharedkey", "accountkey",
        "sharedaccesssignature", "credentials", "key", "secret"
    );
    private static final Pattern SENSITIVE_PARAMETER = Pattern.compile(
        "(^|[?&;])([a-z_]+)=([^&;\\s,)}\\]]+)", Pattern.CASE_INSENSITIVE
    );
    private static final Pattern SHARED_KEY = Pattern.compile(
        "(?i)(SharedKey\\s+[^:\\s]+:)[^\\s,)}]+"
    );

    @Override
    public void process(Exchange exchange) throws Exception {
        Object caught = exchange.getProperty(EXCEPTION_CAUGHT);
        if (caught instanceof Exception exception) {
            exchange.setProperty(EXCEPTION_CAUGHT, sanitize(exception));
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
        Exception cause = originalCause instanceof Exception causeException
            ? sanitize(causeException, seen, depth + 1) : null;
        String message = exception.getMessage();
        String safeMessage = message == null ? exception.getClass().getSimpleName() : redact(message);
        Exception sanitized = new RuntimeException(safeMessage, cause);
        sanitized.setStackTrace(exception.getStackTrace());
        return sanitized;
    }

    private static String redact(String message) {
        Matcher matcher = SENSITIVE_PARAMETER.matcher(message);
        StringBuffer redacted = new StringBuffer();
        while (matcher.find()) {
            if (SENSITIVE_PARAMETER_NAMES.contains(matcher.group(2).toLowerCase(Locale.ROOT))) {
                String replacement = matcher.group(1) + matcher.group(2) + "=[REDACTED]";
                matcher.appendReplacement(redacted, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(redacted);
        return SHARED_KEY.matcher(redacted.toString()).replaceAll("$1[REDACTED]");
    }
}
