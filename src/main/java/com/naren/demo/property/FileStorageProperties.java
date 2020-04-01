package com.naren.demo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file")
@Configuration
public class FileStorageProperties {
	private String uploadDir;

}
