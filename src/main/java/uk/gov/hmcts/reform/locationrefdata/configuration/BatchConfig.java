package uk.gov.hmcts.reform.locationrefdata.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.locationrefdata.camel.listener.JobResultListener;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdBuildingLocationRouteTask;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdCourtVenueRouteTask;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdOrgServiceMappingRouteTask;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("${lrd-route-task}")
    String lrdTask;

    @Value("${lrd-building-location-route-task}")
    String lrdBuildingLocationLoadTask;

    @Value("${lrd-court-venue-route-task}")
    String lrdCourtVenueLoadTask;

    @Value("${batchjob-name}")
    String jobName;

    @Autowired
    LrdOrgServiceMappingRouteTask lrdOrgServiceMappingRouteTask;

    @Autowired
    private LrdBuildingLocationRouteTask lrdBuildingLocationRouteTask;

    @Autowired
    private LrdCourtVenueRouteTask lrdCourtVenueRouteTask;

    @Autowired
    JobResultListener jobResultListener;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step stepLrdRoute() {
        return steps.get(lrdTask)
                .tasklet(lrdOrgServiceMappingRouteTask)
                .build();
    }

    @Bean
    public Step stepLrdBuildingLocationRoute() {
        return steps.get(lrdBuildingLocationLoadTask)
            .tasklet(lrdBuildingLocationRouteTask)
            .build();
    }

    @Bean
    public Step stepLrdCourtVenueRoute() {
        return steps.get(lrdCourtVenueLoadTask)
            .tasklet(lrdCourtVenueRouteTask)
            .build();
    }

    @Bean
    public Job runRoutesJob() {
        return jobBuilderFactory.get(jobName)
                .start(stepLrdRoute())
                .listener(jobResultListener)
                .on("*").to(stepLrdBuildingLocationRoute())
                .on("*").to(stepLrdCourtVenueRoute())
                .end()
                .build();
    }
}
