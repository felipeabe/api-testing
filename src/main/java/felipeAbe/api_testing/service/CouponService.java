package felipeAbe.api_testing.service;

import felipeAbe.api_testing.client.PartnerClient;
import felipeAbe.api_testing.client.TelemetryClient;
import felipeAbe.api_testing.client.dto.PartnerCouponStatusRequest;
import felipeAbe.api_testing.client.dto.PartnerCouponStatusResponse;
import felipeAbe.api_testing.client.dto.TelemetryRequest;
import felipeAbe.api_testing.controller.dto.CouponHistoryResponse;
import felipeAbe.api_testing.controller.dto.CouponValidationResponse;
import felipeAbe.api_testing.entity.Coupon;
import felipeAbe.api_testing.entity.CouponHistory;
import felipeAbe.api_testing.repository.CouponHistoryRepository;
import felipeAbe.api_testing.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;
    private final CouponHistoryRepository historyRepository;
    private final PartnerClient partnerClient;
    private final TelemetryClient telemetryClient;

    @Value("${coupon.api-key}")
    private String appApiKey;

    public CouponService(CouponRepository couponRepository,
                         CouponHistoryRepository historyRepository,
                         PartnerClient partnerClient,
                         TelemetryClient telemetryClient) {
        this.couponRepository = couponRepository;
        this.historyRepository = historyRepository;
        this.partnerClient = partnerClient;
        this.telemetryClient = telemetryClient;
    }

    public CouponValidationResponse validateCoupon(String couponCode) {
        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

        boolean externalValid = validateWithExternalPartner(couponCode);
        boolean isCouponValid = coupon.isUsable() && externalValid;

        String message = isCouponValid
                ? "Cupom válido! Aproveite seu desconto."
                : "Cupom inválido, expirado ou sem usos restantes.";

        if (isCouponValid) {
            coupon.setRemainingUses(coupon.getRemainingUses() - 1);
            couponRepository.save(coupon);
        }

        historyRepository.save(CouponHistory.from(coupon, isCouponValid));

        return CouponValidationResponse.from(coupon, isCouponValid, message);
    }

    private boolean validateWithExternalPartner(String couponCode) {
        try {

            var request = new PartnerCouponStatusRequest(couponCode);
            PartnerCouponStatusResponse response = partnerClient.getCouponStatus(
                    appApiKey, request);

            logger.info("API External partner - Req - {}, Resp - {}", request, response);

            return "ACTIVE".equals(response.status());

        } catch (Exception e) {
            logger.error("Error in API External partner", e);

            return false;
        }
    }

    public List<CouponHistoryResponse> getHistory(String couponCode) {
        sendTelemetry(couponCode);
        return historyRepository.findByCouponCodeOrderByValidatedAtDesc(couponCode)
                .stream()
                .map(CouponHistoryResponse::from)
                .toList();
    }

    private void sendTelemetry(String couponCode) {
        TelemetryRequest payload =
                new TelemetryRequest(
                        "COUPON_HISTORY_VIEW",
                        couponCode,
                        LocalDateTime.now().toString(),
                        Map.of("environment", "PRODUCTION")
                );

        try {
            telemetryClient.sendEvent(appApiKey, payload);
            logger.info("Telemtry sent {}", payload);
        } catch (Exception e) {
            logger.error("Error while sending telemtry - {}", payload);
        }
    }
}
