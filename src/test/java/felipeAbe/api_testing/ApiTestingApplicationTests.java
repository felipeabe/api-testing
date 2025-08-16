package felipeAbe.api_testing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Import(ServiceConnectionConfig.class)
@SpringBootTest
class ApiTestingApplicationTests {

	@Test
	void contextLoads() {
	}

}
