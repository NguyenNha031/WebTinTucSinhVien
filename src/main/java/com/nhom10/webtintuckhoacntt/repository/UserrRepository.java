package com.nhom10.webtintuckhoacntt.repository;

import com.nhom10.webtintuckhoacntt.model.Userr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserrRepository extends JpaRepository<Userr, Long> {
    Userr findByEmailAndPassword(String email, String password);

}
