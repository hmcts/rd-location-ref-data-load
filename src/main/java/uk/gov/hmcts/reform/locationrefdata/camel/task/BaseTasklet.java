package uk.gov.hmcts.reform.locationrefdata.camel.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.route.DataLoadRoute;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdExecutor;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static uk.gov.hmcts.reform.locationrefdata.camel.constants.LrdDataLoadConstants.IS_READY_TO_AUDIT;

@Slf4j
@Component
public class BaseTasklet {

    @Autowired
    CamelContext camelContext;

    @Autowired
    LrdExecutor lrdExecutor;

    @Autowired
    DataLoadRoute dataLoadRoute;

    @Value("${logging-component-name}")
    private String logComponentName;

    public RepeatStatus execute(String startRoute, List<String> routesToExecute, Boolean doAudit) throws Exception {
        log.info("{}:: ParentRouteTask starts::", logComponentName);
        doAudit = (isEmpty(doAudit)) ? Boolean.FALSE : doAudit;
        camelContext.getGlobalOptions().put(IS_READY_TO_AUDIT, doAudit.toString());
        dataLoadRoute.startRoute(startRoute, routesToExecute);
        String status = lrdExecutor.execute(camelContext, "LRD Route", startRoute);
        log.info("{}:: ParentRouteTask completes with status::{}", logComponentName, status);
        return RepeatStatus.FINISHED;
    }
}
