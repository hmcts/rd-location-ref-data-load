package uk.gov.hmcts.reform.locationrefdata.camel.util;

import lombok.Getter;

@Getter
public class LogDto {

    String logMessage;
    String logComponentName;

    public LogDto(String logMessage, String logComponentName) {
        this.logMessage = logMessage;
        this.logComponentName = logComponentName;
    }
}
