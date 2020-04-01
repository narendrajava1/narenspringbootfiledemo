package com.naren.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.naren.demo.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long>{

	Document findByDocName(String fileName);

}
