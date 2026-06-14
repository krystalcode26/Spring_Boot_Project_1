package net.javaguides.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsBackendApplication.class, args);
	}

}
