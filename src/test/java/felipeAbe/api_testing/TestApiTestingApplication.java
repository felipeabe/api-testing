package felipeAbe.api_testing;

import org.springframework.boot.SpringApplication;


public class TestApiTestingApplication {

	public static void main(String[] args) {
		SpringApplication.from(ApiTestingApplication::main)
				.with(ServiceConnectionConfig.class).run(args);
	}

}
