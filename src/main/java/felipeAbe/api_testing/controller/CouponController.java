package felipeAbe.api_testing.controller;

import felipeAbe.api_testing.controller.dto.CouponHistoryResponse;
import felipeAbe.api_testing.controller.dto.CouponValidationRequest;
import felipeAbe.api_testing.controller.dto.CouponValidationResponse;
import felipeAbe.api_testing.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public CouponValidationResponse validateCoupon(@RequestBody CouponValidationRequest request) {
        return couponService.validateCoupon(request.couponCode());
    }

    @GetMapping("/history")
    public List<CouponHistoryResponse> getCouponHistory(@RequestParam String couponCode) {
        return couponService.getHistory(couponCode);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFound(EntityNotFoundException e){
        return ResponseEntity.notFound().build();
    }
}
