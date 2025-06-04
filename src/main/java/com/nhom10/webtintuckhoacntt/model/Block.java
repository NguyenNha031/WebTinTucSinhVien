package com.nhom10.webtintuckhoacntt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "block")
@Getter
@Setter
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_block")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(name = "id_post")
    private Long idPost;

    @Column(name = "id_page")
    private Long idPage;
}
