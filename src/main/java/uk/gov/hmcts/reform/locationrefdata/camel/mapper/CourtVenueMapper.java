package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
import uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils;

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
        courtVenueRow.put("venue_name", trim(courtVenueType.getVenueName()));
        courtVenueRow.put("is_case_management_location", trim(courtVenueType.getIsCaseManagementLocation()));
        courtVenueRow.put("is_hearing_location", trim(courtVenueType.getIsHearingLocation()));
        courtVenueRow.put("welsh_venue_name", trim(courtVenueType.getWelshVenueName()));
        courtVenueRow.put("is_temporary_location", trim(courtVenueType.getIsTemporaryLocation()));
        courtVenueRow.put("is_nightingale_court", trim(courtVenueType.getIsNightingaleCourt()));
        courtVenueRow.put("location_type", trim(courtVenueType.getLocationType()));
        courtVenueRow.put("parent_location", trim(courtVenueType.getParentLocation()));
        courtVenueRow.put("welsh_court_name", trim(courtVenueType.getWelshCourtName()));
        courtVenueRow.put("uprn", trim(courtVenueType.getUprn()));
        courtVenueRow.put("venue_ou_code", trim(courtVenueType.getVenueOuCode()));
        courtVenueRow.put("mrd_building_location_id", trim(courtVenueType.getMrdBuildingLocationId()));
        courtVenueRow.put("mrd_venue_id", trim(courtVenueType.getMrdVenueId()));
        courtVenueRow.put("service_url", trim(courtVenueType.getServiceUrl()));
        courtVenueRow.put("fact_url", trim(courtVenueType.getFactUrl()));
        courtVenueRow.put("mrd_created_time", LrdLoadUtils.getDateTimeStamp(courtVenueType.getMrdCreatedTime()));
        courtVenueRow.put("mrd_updated_time", LrdLoadUtils.getDateTimeStamp(courtVenueType.getMrdUpdatedTime()));
        courtVenueRow.put("mrd_deleted_time", LrdLoadUtils.getDateTimeStamp(courtVenueType.getMrdDeletedTime()));
        courtVenueRow.put("external_short_name", trim(courtVenueType.getShortCourtName()));


        return courtVenueRow;
    }
}
