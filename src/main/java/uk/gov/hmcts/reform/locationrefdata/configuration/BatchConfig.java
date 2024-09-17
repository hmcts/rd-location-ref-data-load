package uk.gov.hmcts.reform.locationrefdata.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import uk.gov.hmcts.reform.locationrefdata.camel.listener.JobResultListener;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdBuildingLocationRouteTask;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdCourtVenueRouteTask;
import uk.gov.hmcts.reform.locationrefdata.camel.task.LrdOrgServiceMappingRouteTask;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

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

    @Bean
    public Step stepLrdRoute(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager) {
        return new StepBuilder(lrdTask, jobRepository)
            .tasklet(lrdOrgServiceMappingRouteTask, transactionManager)
            .build();
    }

    @Bean
    public Step stepLrdBuildingLocationRoute(JobRepository jobRepository,
                                             PlatformTransactionManager transactionManager) {
        return new StepBuilder(lrdBuildingLocationLoadTask, jobRepository)
            .tasklet(lrdBuildingLocationRouteTask, transactionManager)
            .build();
    }

    @Bean
    public Step stepLrdCourtVenueRoute(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager) {
        return new StepBuilder(lrdCourtVenueLoadTask, jobRepository)
            .tasklet(lrdCourtVenueRouteTask, transactionManager)
            .build();
    }

    @Bean
    public Job runRoutesJob(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager) {
        return new JobBuilder(jobName, jobRepository)
                .start(stepLrdRoute(jobRepository, transactionManager))
                .listener(jobResultListener)
                .on("*").to(stepLrdBuildingLocationRoute(jobRepository, transactionManager))
                .on("*").to(stepLrdCourtVenueRoute(jobRepository, transactionManager))
                .end()
                .build();
    }
}
