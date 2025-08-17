package felipeAbe.api_testing;

import org.springframework.boot.SpringApplication;

import static felipeAbe.api_testing.ContainerConfig.getProperties;
import static felipeAbe.api_testing.ContainerConfig.wireMockContainer;


public class TestApiTestingApplication {

	public static void main(String[] args) {
		wireMockContainer.start();
		getProperties().forEach(System::setProperty);
		SpringApplication.from(ApiTestingApplication::main)
				.with(ServiceConnectionConfig.class)
				.run(args);
	}

}
