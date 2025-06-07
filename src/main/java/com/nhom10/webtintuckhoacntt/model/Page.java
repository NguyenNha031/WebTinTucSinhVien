package com.nhom10.webtintuckhoacntt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "page")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_page")
    private Long idPage;

    private String title;

    @Column(name = "id_menu")
    private Long idMenu;
    @Column(columnDefinition = "TEXT")
    private String content;

}
