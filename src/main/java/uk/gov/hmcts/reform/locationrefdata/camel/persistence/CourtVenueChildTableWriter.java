package uk.gov.hmcts.reform.locationrefdata.camel.persistence;

import java.util.Map;

public interface CourtVenueChildTableWriter {

    void upsert(Long courtVenueId, Map<String, Object> courtVenueRow);
}
