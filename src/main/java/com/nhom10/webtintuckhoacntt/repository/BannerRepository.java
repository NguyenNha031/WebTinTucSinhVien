package com.nhom10.webtintuckhoacntt.repository;
import com.nhom10.webtintuckhoacntt.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByOrderByIdAsc();
}
