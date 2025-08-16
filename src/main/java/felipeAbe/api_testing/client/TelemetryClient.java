package felipeAbe.api_testing.client;

import felipeAbe.api_testing.client.dto.TelemetryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "telemetryClient", url = "${telemetry.api.url}")
public interface TelemetryClient {

    @PostMapping("/v1/events")
    void sendEvent(@RequestHeader("x-api-key") String apiKey, TelemetryRequest request);

}
