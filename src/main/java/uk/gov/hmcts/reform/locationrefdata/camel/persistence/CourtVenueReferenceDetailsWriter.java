package uk.gov.hmcts.reform.locationrefdata.camel.persistence;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class CourtVenueReferenceDetailsWriter implements CourtVenueChildTableWriter {

    private static final String UPSERT_SQL = """
        INSERT INTO court_venue_reference_details (
            court_venue_id,
            welsh_court_name,
            uprn,
            venue_ou_code,
            mrd_building_location_id,
            mrd_venue_id,
            service_url,
            fact_url,
            external_short_name,
            welsh_external_short_name,
            created_time,
            updated_time
        ) VALUES (
            :court_venue_id,
            :welsh_court_name,
            :uprn,
            :venue_ou_code,
            :mrd_building_location_id,
            :mrd_venue_id,
            :service_url,
            :fact_url,
            :external_short_name,
            :welsh_external_short_name,
            NOW() AT TIME ZONE 'utc',
            NOW() AT TIME ZONE 'utc'
        )
        ON CONFLICT (court_venue_id) DO UPDATE SET
            welsh_court_name = :welsh_court_name,
            uprn = :uprn,
            venue_ou_code = :venue_ou_code,
            mrd_building_location_id = :mrd_building_location_id,
            mrd_venue_id = :mrd_venue_id,
            service_url = :service_url,
            fact_url = :fact_url,
            external_short_name = :external_short_name,
            welsh_external_short_name = :welsh_external_short_name,
            updated_time = NOW() AT TIME ZONE 'utc'
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CourtVenueReferenceDetailsWriter(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void upsert(Long courtVenueId, Map<String, Object> courtVenueRow) {
        MapSqlParameterSource parameters = new MapSqlParameterSource(courtVenueRow);
        parameters.addValue("court_venue_id", courtVenueId);
        jdbcTemplate.update(UPSERT_SQL, parameters);
    }
}
