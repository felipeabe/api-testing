package felipeAbe.api_testing.controller.dto;



import felipeAbe.api_testing.entity.CouponHistory;

import java.time.LocalDateTime;

public record CouponHistoryResponse(
        String couponCode,
        boolean valid,
        int discountPercentage,
        LocalDateTime validatedAt) {

    public static CouponHistoryResponse from(CouponHistory history) {
        return new CouponHistoryResponse(
                history.getCouponCode(),
                history.isValid(),
                history.getDiscountPercentage(),
                history.getValidatedAt()
        );
    }
}
