package com.nhom10.webtintuckhoacntt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "banner")
@Getter @Setter
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banner")
    private Long id;

    private String anh;

    private String mota;

    @Column(name = "id_post")
    private Long idPost;
}

