package uk.gov.hmcts.reform.locationrefdata.camel.task;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.data.ingestion.camel.route.DataLoadRoute;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdExecutor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(SpringExtension.class)
class LrdBuildingLocationRouteTaskTest {

    @Spy
    LrdBuildingLocationRouteTask buildingLocationRouteTask = new LrdBuildingLocationRouteTask();

    DataLoadRoute dataLoadRoute = mock(DataLoadRoute.class);

    LrdExecutor lrdExecutor = mock(LrdExecutor.class);

    CamelContext camelContext = new DefaultCamelContext();

    @BeforeEach
    public void init() {
        setField(buildingLocationRouteTask, "logComponentName", "testlogger");
        setField(buildingLocationRouteTask, "dataLoadRoute", dataLoadRoute);
        setField(buildingLocationRouteTask, "lrdExecutor", lrdExecutor);
        setField(buildingLocationRouteTask, "camelContext", camelContext);
    }

    @Test
    void testExecute() throws Exception {
        doNothing().when(dataLoadRoute).startRoute(anyString(), anyList());
        when(lrdExecutor.execute(any(), any(), any())).thenReturn("success");
        assertEquals(RepeatStatus.FINISHED, buildingLocationRouteTask
            .execute(anyString(), anyList(), anyBoolean()));
        verify(buildingLocationRouteTask, times(1))
            .execute(anyString(), anyList(), anyBoolean());
    }

    @Test
    void testExecute_NoAuditPreference() throws Exception {
        doNothing().when(dataLoadRoute).startRoute(anyString(), anyList());
        when(lrdExecutor.execute(any(), any(), any())).thenReturn("success");
        assertEquals(RepeatStatus.FINISHED, buildingLocationRouteTask
            .execute(anyString(), anyList(), isNull()));
        verify(buildingLocationRouteTask, times(1))
            .execute(anyString(), anyList(), isNull());
    }
}
