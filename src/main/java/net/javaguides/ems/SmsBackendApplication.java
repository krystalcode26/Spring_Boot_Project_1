package net.javaguides.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class SmsBackendApplication {

	public static void main(String[] args) {
		System.out.println("Hello EC2");
		SpringApplication.run(SmsBackendApplication.class, args);
	}

}
