package felipeAbe.api_testing.client;


import felipeAbe.api_testing.client.dto.PartnerCouponStatusRequest;
import felipeAbe.api_testing.client.dto.PartnerCouponStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "partnerClient", url = "${partner.api.url}")
public interface PartnerClient {

    @PostMapping(value = "/api/partners/v1/coupons/status", consumes = "application/json", produces = "application/json")
    PartnerCouponStatusResponse getCouponStatus(@RequestHeader("x-api-key") String apiKey,
                                                PartnerCouponStatusRequest request);

}
