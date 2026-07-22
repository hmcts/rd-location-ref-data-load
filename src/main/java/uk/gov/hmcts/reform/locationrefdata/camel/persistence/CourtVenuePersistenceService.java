package uk.gov.hmcts.reform.locationrefdata.camel.persistence;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.locationrefdata.camel.binder.CourtVenue;
import uk.gov.hmcts.reform.locationrefdata.camel.mapper.CourtVenueMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class CourtVenuePersistenceService {

    private static final String UPSERT_SQL = """
        INSERT INTO court_venue (
            epimms_id,
            site_name,
            court_name,
            court_status,
            court_open_date,
            region_id,
            court_type_id,
            cluster_id,
            open_for_public,
            court_address,
            postcode,
            phone_number,
            closed_date,
            court_location_code,
            dx_address,
            welsh_site_name,
            welsh_court_address,
            venue_name,
            is_case_management_location,
            is_hearing_location,
            welsh_venue_name,
            is_temporary_location,
            is_nightingale_court,
            location_type,
            parent_location,
            welsh_court_name,
            uprn,
            venue_ou_code,
            mrd_building_location_id,
            mrd_venue_id,
            service_url,
            fact_url,
            mrd_created_time,
            mrd_updated_time,
            mrd_deleted_time,
            external_short_name,
            welsh_external_short_name,
            created_time,
            updated_time
        ) VALUES (
            :epimms_id,
            :site_name,
            :court_name,
            :court_status,
            TO_DATE(NULLIF(:court_open_date, ''), 'dd/MM/yyyy'),
            :region_id,
            :court_type_id,
            :cluster_id,
            CAST(:open_for_public AS boolean),
            :court_address,
            :postcode,
            :phone_number,
            TO_DATE(NULLIF(:closed_date, ''), 'dd/MM/yyyy'),
            :court_location_code,
            :dx_address,
            :welsh_site_name,
            :welsh_court_address,
            :venue_name,
            :is_case_management_location,
            :is_hearing_location,
            :welsh_venue_name,
            :is_temporary_location,
            :is_nightingale_court,
            :location_type,
            :parent_location,
            :welsh_court_name,
            :uprn,
            :venue_ou_code,
            :mrd_building_location_id,
            :mrd_venue_id,
            :service_url,
            :fact_url,
            :mrd_created_time,
            :mrd_updated_time,
            :mrd_deleted_time,
            :external_short_name,
            :welsh_external_short_name,
            NOW() AT TIME ZONE 'utc',
            NOW() AT TIME ZONE 'utc'
        )
        ON CONFLICT (epimms_id, court_type_id) DO UPDATE SET
            epimms_id = :epimms_id,
            site_name = :site_name,
            court_name = :court_name,
            court_status = :court_status,
            court_open_date = TO_DATE(NULLIF(:court_open_date, ''), 'dd/MM/yyyy'),
            region_id = :region_id,
            court_type_id = :court_type_id,
            cluster_id = :cluster_id,
            open_for_public = CAST(:open_for_public AS boolean),
            court_address = :court_address,
            postcode = :postcode,
            phone_number = :phone_number,
            closed_date = TO_DATE(NULLIF(:closed_date, ''), 'dd/MM/yyyy'),
            dx_address = :dx_address,
            welsh_site_name = :welsh_site_name,
            welsh_court_address = :welsh_court_address,
            venue_name = :venue_name,
            is_case_management_location = :is_case_management_location,
            is_hearing_location = :is_hearing_location,
            welsh_venue_name = :welsh_venue_name,
            is_temporary_location = :is_temporary_location,
            is_nightingale_court = :is_nightingale_court,
            location_type = :location_type,
            parent_location = :parent_location,
            welsh_court_name = :welsh_court_name,
            uprn = :uprn,
            venue_ou_code = :venue_ou_code,
            mrd_building_location_id = :mrd_building_location_id,
            mrd_venue_id = :mrd_venue_id,
            service_url = :service_url,
            fact_url = :fact_url,
            mrd_created_time = :mrd_created_time,
            mrd_updated_time = :mrd_updated_time,
            mrd_deleted_time = :mrd_deleted_time,
            external_short_name = :external_short_name,
            welsh_external_short_name = :welsh_external_short_name,
            updated_time = NOW() AT TIME ZONE 'utc'
        RETURNING court_venue_id
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CourtVenueMapper courtVenueMapper;
    private final List<CourtVenueChildTableWriter> childTableWriters;

    public CourtVenuePersistenceService(DataSource dataSource,
                                        CourtVenueMapper courtVenueMapper,
                                        List<CourtVenueChildTableWriter> childTableWriters) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.courtVenueMapper = courtVenueMapper;
        this.childTableWriters = childTableWriters;
    }

    public void persist(List<CourtVenue> courtVenues) {
        for (CourtVenue courtVenue : courtVenues) {
            Map<String, Object> courtVenueRow = courtVenueMapper.getMap(courtVenue);
            Long courtVenueId = jdbcTemplate.queryForObject(
                UPSERT_SQL,
                new MapSqlParameterSource(courtVenueRow),
                Long.class
            );
            childTableWriters.forEach(writer -> writer.upsert(courtVenueId, courtVenueRow));
        }
    }
}
