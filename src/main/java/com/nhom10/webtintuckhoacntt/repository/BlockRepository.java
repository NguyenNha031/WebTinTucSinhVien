package com.nhom10.webtintuckhoacntt.repository;

import com.nhom10.webtintuckhoacntt.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByIdPost(Long idPost);
    List<Block> findByIdPage(Long idPage);

}
