package felipeAbe.api_testing;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
class ServiceConnectionConfig{

	@Bean
	@ServiceConnection
	MySQLContainer<?> mysqlContainer() {
		return new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

	}


}
