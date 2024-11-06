package uk.gov.hmcts.reform.locationrefdata.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class DataQualityCheckConfiguration {

    @Value("${zero-byte-characters}")
    public List<String> zeroByteCharacters;
}
