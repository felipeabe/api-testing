package felipeAbe.api_testing.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import felipeAbe.api_testing.ContainerConfig;
import felipeAbe.api_testing.ServiceConnectionConfig;
import felipeAbe.api_testing.entity.Coupon;
import felipeAbe.api_testing.repository.CouponHistoryRepository;
import felipeAbe.api_testing.repository.CouponRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(ServiceConnectionConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponControllerIT extends ContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponHistoryRepository couponHistoryRepository;

    @BeforeEach
    public void beforeEach(){
        WireMock.resetAllRequests();
        couponRepository.deleteAll();
        couponHistoryRepository.deleteAll();
    }

    @Nested
    class validate{

        @Nested
        class validCouponScenarios {

            String couponCode="PROMO50";
            int discountPercentage=50;
            int remainingUsages=10;
            LocalDateTime validUntil=LocalDateTime.now().plusDays(7);


            private ResultActions setupArrangeAct() throws Exception {
                //arrange
                couponRepository.save(new Coupon(couponCode, discountPercentage, remainingUsages, validUntil));

                //act
                var result=mockMvc.perform(
                        post("/api/v1/coupons/validate")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(String.format("""
                                        {
                                            "couponCode": "%s"
                                        }
                                        """, couponCode))
                );
                return result;
            }

            @Test
            void shouldReturnCorrectApiBody() throws Exception {

                var result = setupArrangeAct();

                //assert
                result.andExpect(status().is(200))
                        .andExpect(jsonPath("$.couponCode").value(couponCode))
                        .andExpect(jsonPath("$.valid").value(true))
                        .andExpect(jsonPath("$.discountPercentage").value(discountPercentage))
                        .andExpect(jsonPath("$.validatedAt").isNotEmpty())
                        .andExpect(jsonPath("$.remainingUses").value(remainingUsages-1))
                        .andExpect(jsonPath("$.message").value("Cupom válido! Aproveite seu desconto."));

            }


            @Test
            void shouldCallExternalApi() throws Exception {

                setupArrangeAct();

                WireMock.verify(1, newRequestPattern()
                        .withUrl("/api/partners/v1/coupons/status")
                        .withHeader("x-api-key", equalTo("valid-api-key"))
                        .withRequestBody(equalToJson(String.format("""
                                {
                                    "couponCode": "%s"
                                }
                                """, couponCode)))
                );
            }

            @Test
            void shouldDecreaseCouponUsageOnDatabase() throws Exception {

                setupArrangeAct();

                //assert
                var coupon=couponRepository.findById(couponCode);
                assertTrue(coupon.isPresent());
                assertEquals(remainingUsages-1, coupon.get().getRemainingUses());
            }

            @Test
            void shouldInsertToHistoryTable() throws Exception {

                setupArrangeAct();

                //assert
                var couponHistory=couponHistoryRepository.findByCouponCodeOrderByValidatedAtDesc(couponCode);
                assertEquals(couponCode, couponHistory.getFirst().getCouponCode());
                assertNotNull(couponHistory.getFirst().getId());
                assertNotNull(couponHistory.getFirst().getValidatedAt());
                assertEquals(couponCode,couponHistory.getFirst().getCouponCode());
                assertEquals(discountPercentage, couponHistory.getFirst().getDiscountPercentage() );

            }
        }
    }

}
