package com.nhom10.webtintuckhoacntt.repository;

import com.nhom10.webtintuckhoacntt.model.Page;
import org.springframework.cglib.core.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

}
