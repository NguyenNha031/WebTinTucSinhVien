package com.nhom10.webtintuckhoacntt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "userr")
public class Userr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_userr")
    private Long id;

    private String role;

    private String email;

    private String fullname;

    private String password;

    private String sdt;
}
