package uk.gov.hmcts.reform.locationrefdata.camel.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LrdBuildingLocationRouteTask extends BaseTasklet implements Tasklet {

    @Value("${lrd-building-location-start-route}")
    private String startRoute;

    @Value("${building-locations-routes-to-execute}")
    List<String> routesToExecute;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        return super.execute(startRoute, routesToExecute, Boolean.FALSE);
    }
}
