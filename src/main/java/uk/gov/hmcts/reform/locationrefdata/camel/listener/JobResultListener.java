package uk.gov.hmcts.reform.locationrefdata.camel.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.service.ArchivalBlobServiceImpl;

@Component
@Slf4j
public class JobResultListener implements JobExecutionListener {

    @Value("${logging-component-name}")
    private String logComponentName;

    @Autowired
    ArchivalBlobServiceImpl archivalBlobService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("{}:: Batch Job execution Started", logComponentName);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("{}:: Calling Archival Service", logComponentName);
        archivalBlobService.executeArchiving();
    }
}
