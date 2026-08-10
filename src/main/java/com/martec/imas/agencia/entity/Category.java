package com.martec.imas.agencia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String icon;

    private Integer displayOrder;

    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Service> services;
}