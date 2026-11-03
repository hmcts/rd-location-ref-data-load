package uk.gov.hmcts.reform.locationrefdata.camel.processor;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static uk.gov.hmcts.reform.data.ingestion.camel.util.MappingConstants.SCHEDULER_START_TIME;

@Component
public class CourtVenueFileDataStore {

    private static final String DEFAULT_JOB_KEY = "default";
    private static final ConcurrentMap<String, List<CourtVenue>> COURT_VENUES_BY_JOB = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Set<String>> MRD_VENUE_IDS_BY_JOB = new ConcurrentHashMap<>();

    public String jobKey(Exchange exchange) {
        if (exchange == null || exchange.getContext() == null) {
            return DEFAULT_JOB_KEY;
        }
        return StringUtils.defaultIfBlank(exchange.getContext().getGlobalOption(SCHEDULER_START_TIME), DEFAULT_JOB_KEY);
    }

    public void reset(String jobKey) {
        COURT_VENUES_BY_JOB.remove(jobKey);
        MRD_VENUE_IDS_BY_JOB.remove(jobKey);
    }

    public boolean registerMrdVenueIds(String jobKey, List<CourtVenue> courtVenues) {
        Set<String> seenMrdVenueIds = MRD_VENUE_IDS_BY_JOB.computeIfAbsent(
            jobKey,
            key -> ConcurrentHashMap.newKeySet()
        );
        for (CourtVenue courtVenue : courtVenues) {
            String mrdVenueId = StringUtils.trim(courtVenue.getMrdVenueId());
            if (StringUtils.isNotBlank(mrdVenueId) && !seenMrdVenueIds.add(mrdVenueId)) {
                return false;
            }
        }
        return true;
    }

    public void addAll(String jobKey, List<CourtVenue> courtVenues) {
        if (courtVenues.isEmpty()) {
            return;
        }
        COURT_VENUES_BY_JOB.compute(jobKey, (key, existingCourtVenues) -> {
            List<CourtVenue> updatedCourtVenues = new ArrayList<>();
            if (existingCourtVenues != null) {
                updatedCourtVenues.addAll(existingCourtVenues);
            }
            updatedCourtVenues.addAll(courtVenues);
            return updatedCourtVenues;
        });
    }

    public List<CourtVenue> remove(String jobKey) {
        List<CourtVenue> courtVenues = COURT_VENUES_BY_JOB.remove(jobKey);
        return courtVenues == null ? List.of() : List.copyOf(courtVenues);
    }
}
