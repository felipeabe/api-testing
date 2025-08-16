package felipeAbe.api_testing.client.dto;

public record PartnerCouponStatusResponse(
        String couponCode,
        String status,
        int discountPercentage,
        String validUntil,
        String reason
) {}
