package com.nhom10.webtintuckhoacntt.repository;

import com.nhom10.webtintuckhoacntt.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

}
