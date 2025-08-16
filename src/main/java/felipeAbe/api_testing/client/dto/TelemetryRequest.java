package felipeAbe.api_testing.client.dto;

import java.util.Map;

public record TelemetryRequest(String eventType,
                               String couponCode,
                               String timestamp,
                               Map<String, String> metadata) {

}
