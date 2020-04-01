package com.naren.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class NarenSpringbootfileDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NarenSpringbootfileDemoApplication.class, args);
	}

}
