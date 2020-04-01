package com.naren.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.naren.demo.exception.FileStorageException;
import com.naren.demo.property.FileStorageProperties;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@SuppressWarnings("rawtypes")
public class FileStorageService {

	//	private final Path fileStorageLocation;

	@Autowired
	FileStorageProperties fileStorageProperties;

	public ResponseEntity multiUpload(MultipartFile[] files) {
		final List<Object> fileDownloadUrls = new ArrayList<>();
		Arrays.asList(files)
		.stream()
		.forEach(file -> fileDownloadUrls.add(uploadToLocalFileSystem(file).getBody()));
		return ResponseEntity.ok(fileDownloadUrls);


	}

	public ResponseEntity uploadToLocalFileSystem(MultipartFile file){

		final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		final Path path = Paths.get(fileStorageProperties.getUploadDir() + fileName);
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}catch(final IOException e) {
			log.error("something went wring in the file {}", e);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
		}

		final String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/download/")
				.path(path.getFileName().toString())
				.toUriString();

		return ResponseEntity.ok(fileDownloadUri);

	}

}
