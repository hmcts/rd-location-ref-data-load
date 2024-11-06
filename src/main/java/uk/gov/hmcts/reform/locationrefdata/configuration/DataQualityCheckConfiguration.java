package uk.gov.hmcts.reform.locationrefdata.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataQualityCheckConfiguration {

    @Value("${zero-byte-characters}")
    private List<String> zeroByteCharacters;

    public List<String> getZeroByteCharacters() {
        return zeroByteCharacters;
    }

    public void setZeroByteCharacters(List<String> zeroByteCharacters) {
        this.zeroByteCharacters = zeroByteCharacters;
    }
}
