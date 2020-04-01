package com.naren.demo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.naren.demo.model.Document;
import com.naren.demo.property.FileStorageProperties;
import com.naren.demo.repo.DocumentRepository;
import com.naren.demo.service.FileStorageService;

@RestController
@SuppressWarnings("rawtypes")
@RequestMapping("/files")
public class FileController {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	FileStorageProperties fileStorageProperties;

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
		final Path path = Paths.get( fileStorageProperties.getUploadDir() + fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/download/{fileName:.+}/db")
	public ResponseEntity downloadFromDB(@PathVariable String fileName) {
		final Document document = documentRepository.findByDocName(fileName);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(document.getFile());
	}

	@PostMapping("/multi-upload")
	public ResponseEntity multiUpload(@RequestParam("files") MultipartFile[] files) {
		return fileStorageService.multiUpload(files);
	}

	@PostMapping("/upload/db")
	public ResponseEntity uploadToDB(@RequestParam("file") MultipartFile file) {
		final Document doc = new Document();
		final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		doc.setDocName(fileName);
		try {
			doc.setFile(file.getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		documentRepository.save(doc);
		final String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/download/")
				.path(fileName).path("/db")
				.toUriString();
		return ResponseEntity.ok(fileDownloadUri);
	}

	@PostMapping("/upload")
	public ResponseEntity uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) {
		return fileStorageService.uploadToLocalFileSystem(file);
	}


}
