package felipeAbe.api_testing.repository;


import felipeAbe.api_testing.entity.CouponHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {

    List<CouponHistory> findByCouponCodeOrderByValidatedAtDesc(String couponCode);
}