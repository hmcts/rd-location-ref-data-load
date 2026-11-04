package uk.gov.hmcts.reform.locationrefdata.camel.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.data.ingestion.camel.mapper.IMapper;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;

import java.util.Map;

import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.toBooleanFlag;
import static uk.gov.hmcts.reform.locationrefdata.camel.util.LrdLoadUtils.trim;

@Component
public class CourtVenueMapper implements IMapper {

    @Override
    public Map<String, Object> getMap(Object courtVenue) {
        CourtVenue courtVenueType = (CourtVenue) courtVenue;

        Map<String, Object> courtVenueRow = CommonMapper.getMap(courtVenueType.getEpimmsId(),
                                                                courtVenueType.getRegionId(),
                                                                courtVenueType.getClusterId(),
                                                                courtVenueType.getPostcode(),
                                                                courtVenueType.getUprn(),
                                                                courtVenueType.getMrdBuildingLocationId(),
                                                                courtVenueType.getMrdCreatedTime(),
                                                                courtVenueType.getMrdUpdatedTime(),
                                                                courtVenueType.getMrdDeletedTime());

        courtVenueRow.put("epimms_id", trim(courtVenueType.getEpimmsId()));
        courtVenueRow.put("region_id", trim(courtVenueType.getRegionId()));
        courtVenueRow.put("cluster_id", trim(courtVenueType.getClusterId()));
        courtVenueRow.put("site_name", courtVenueType.getSiteName());
        courtVenueRow.put("court_name", courtVenueType.getCourtName());
        courtVenueRow.put("court_status", trim(courtVenueType.getCourtStatus()));
        courtVenueRow.put("court_open_date", courtVenueType.getCourtOpenDate());
        courtVenueRow.put("court_type_id", courtVenueType.getCourtTypeId());
        courtVenueRow.put("open_for_public", courtVenueType.getOpenForPublic());
        courtVenueRow.put("court_address", courtVenueType.getCourtAddress());
        courtVenueRow.put("postcode", trim(courtVenueType.getPostcode()));
        courtVenueRow.put("phone_number", trim(courtVenueType.getPhoneNumber()));
        courtVenueRow.put("closed_date", courtVenueType.getClosedDate());
        courtVenueRow.put("court_location_code", trim(courtVenueType.getCourtLocationCode()));
        courtVenueRow.put("dx_address", courtVenueType.getDxAddress());
        courtVenueRow.put("welsh_site_name", courtVenueType.getWelshSiteName());
        courtVenueRow.put("welsh_court_address", courtVenueType.getWelshCourtAddress());
        courtVenueRow.put("venue_name", courtVenueType.getVenueName());
        courtVenueRow.put("is_case_management_location",
                          toBooleanFlag(courtVenueType.getIsCaseManagementLocation()));
        courtVenueRow.put("is_hearing_location", toBooleanFlag(courtVenueType.getIsHearingLocation()));
        courtVenueRow.put("welsh_venue_name", courtVenueType.getWelshVenueName());
        courtVenueRow.put("is_temporary_location", toBooleanFlag(courtVenueType.getIsTemporaryLocation()));
        courtVenueRow.put("is_nightingale_court", toBooleanFlag(courtVenueType.getIsNightingaleCourt()));
        courtVenueRow.put("location_type", trim(courtVenueType.getLocationType()));
        courtVenueRow.put("parent_location", trim(courtVenueType.getParentLocation()));
        courtVenueRow.put("welsh_court_name", courtVenueType.getWelshCourtName());
        courtVenueRow.put("uprn", trim(courtVenueType.getUprn()));
        courtVenueRow.put("venue_ou_code", trim(courtVenueType.getVenueOuCode()));
        courtVenueRow.put("mrd_building_location_id", courtVenueType.getMrdBuildingLocationId());
        courtVenueRow.put("mrd_venue_id", trim(courtVenueType.getMrdVenueId()));
        courtVenueRow.put("service_url", trim(courtVenueType.getServiceUrl()));
        courtVenueRow.put("fact_url", trim(courtVenueType.getFactUrl()));
        courtVenueRow.put("external_short_name", courtVenueType.getExternalShortName());
        courtVenueRow.put("welsh_external_short_name", courtVenueType.getWelshExternalShortName());
        courtVenueRow.put("service_code", trim(courtVenueType.getServiceCode()));
        courtVenueRow.put("district_registry_venue_id", trim(courtVenueType.getDistrictRegistryVenueId()));
        courtVenueRow.put("appeal_centre_venue_id", trim(courtVenueType.getAppealCentreVenueId()));
        return courtVenueRow;
    }
}
