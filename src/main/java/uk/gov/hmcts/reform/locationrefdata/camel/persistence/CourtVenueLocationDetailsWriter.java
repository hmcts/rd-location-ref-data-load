package uk.gov.hmcts.reform.locationrefdata.camel.persistence;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
public class CourtVenueLocationDetailsWriter implements CourtVenueChildTableWriter {

    private static final String UPSERT_SQL = """
        INSERT INTO court_venue_location_details (
            court_venue_id,
            venue_name,
            is_case_management_location,
            is_hearing_location,
            welsh_venue_name,
            is_temporary_location,
            is_nightingale_court,
            location_type,
            parent_location,
            created_time,
            updated_time
        ) VALUES (
            :court_venue_id,
            :venue_name,
            :is_case_management_location,
            :is_hearing_location,
            :welsh_venue_name,
            :is_temporary_location,
            :is_nightingale_court,
            :location_type,
            :parent_location,
            NOW() AT TIME ZONE 'utc',
            NOW() AT TIME ZONE 'utc'
        )
        ON CONFLICT (court_venue_id) DO UPDATE SET
            venue_name = :venue_name,
            is_case_management_location = :is_case_management_location,
            is_hearing_location = :is_hearing_location,
            welsh_venue_name = :welsh_venue_name,
            is_temporary_location = :is_temporary_location,
            is_nightingale_court = :is_nightingale_court,
            location_type = :location_type,
            parent_location = :parent_location,
            updated_time = NOW() AT TIME ZONE 'utc'
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CourtVenueLocationDetailsWriter(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void upsert(Long courtVenueId, Map<String, Object> courtVenueRow) {
        MapSqlParameterSource parameters = new MapSqlParameterSource(courtVenueRow);
        parameters.addValue("court_venue_id", courtVenueId);
        jdbcTemplate.update(UPSERT_SQL, parameters);
    }
}
