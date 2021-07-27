package uk.gov.hmcts.reform.locationrefdata.camel.task;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.repeat.RepeatStatus;
import uk.gov.hmcts.reform.data.ingestion.camel.route.DataLoadRoute;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class LrdOrgServiceMappingRouteTaskTest {

    LrdOrgServiceMappingRouteTask lrdOrgServiceMappingRouteTask = spy(new LrdOrgServiceMappingRouteTask());

    DataLoadRoute dataLoadRoute = mock(DataLoadRoute.class);

    LrdExecutor lrdExecutor = mock(LrdExecutor.class);

    CamelContext camelContext = new DefaultCamelContext();

    @BeforeEach
    public void init() {
        setField(lrdOrgServiceMappingRouteTask, "logComponentName", "testlogger");
        setField(lrdOrgServiceMappingRouteTask, "dataLoadRoute", dataLoadRoute);
        setField(lrdOrgServiceMappingRouteTask, "lrdExecutor", lrdExecutor);
        setField(lrdOrgServiceMappingRouteTask, "camelContext", camelContext);
    }

    @Test
    void testExecute() throws Exception {
        doNothing().when(dataLoadRoute).startRoute(anyString(), anyList());
        when(lrdExecutor.execute(any(), any(), any())).thenReturn("success");
        assertEquals(RepeatStatus.FINISHED, lrdOrgServiceMappingRouteTask
            .execute(anyString(), anyList(), anyBoolean()));
        verify(lrdOrgServiceMappingRouteTask, times(1))
            .execute(anyString(), anyList(), anyBoolean());
        verify(dataLoadRoute, times(1)).startRoute(any(), any());
    }
}
