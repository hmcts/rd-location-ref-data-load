package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trimNumeric;

@Component
public class CourtVenueMapper implements IMapper {

    @Override
    public Map<String, Object> getMap(Object courtVenue) {
        CourtVenue courtVenueType = (CourtVenue) courtVenue;
        Map<String, Object> courtVenueRow = new HashMap<>();
        courtVenueRow.put("epimms_id", trim(courtVenueType.getEpimmsId()));
        courtVenueRow.put("site_name", trim(courtVenueType.getSiteName()));
        courtVenueRow.put("court_name", trim(courtVenueType.getCourtName()));
        courtVenueRow.put("court_status", trim(courtVenueType.getCourtStatus()));
        courtVenueRow.put("court_open_date", courtVenueType.getCourtOpenDate());
        courtVenueRow.put("region_id", trimNumeric(courtVenueType.getRegionId()));
        courtVenueRow.put("court_type_id", courtVenueType.getCourtTypeId());
        courtVenueRow.put("cluster_id", trimNumeric(courtVenueType.getClusterId()));
        courtVenueRow.put("open_for_public", courtVenueType.getOpenForPublic());
        courtVenueRow.put("court_address", trim(courtVenueType.getCourtAddress()));
        courtVenueRow.put("postcode", trim(courtVenueType.getPostcode()));
        courtVenueRow.put("phone_number", trim(courtVenueType.getPhoneNumber()));
        courtVenueRow.put("closed_date", courtVenueType.getClosedDate());
        courtVenueRow.put("court_location_code", trim(courtVenueType.getCourtLocationCode()));
        courtVenueRow.put("dx_address", trim(courtVenueType.getDxAddress()));
        courtVenueRow.put("welsh_site_name", trim(courtVenueType.getWelshSiteName()));
        courtVenueRow.put("welsh_court_address", trim(courtVenueType.getWelshCourtAddress()));

        return courtVenueRow;
    }
}
