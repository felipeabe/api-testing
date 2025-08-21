package felipeAbe.api_testing;

import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class ServiceConnectionConfig{


	@Bean
	@ServiceConnection
	MySQLContainer<?> mysqlContainer() {
		var c=new MySQLContainer<>(DockerImageName.parse("mysql:9.1.0"))
				.withUsername("admin")
				.withPassword("123")
				.withDatabaseName("promowisedb");
		c.setPortBindings(List.of("3306:3306"));

		return c;
	}


}
