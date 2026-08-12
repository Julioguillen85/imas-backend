package com.martec.imas.agencia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "name")
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    private String company;

    private String operationType;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String source; // "AI_ASSISTANT" | "LANDING_PAGE"

    @Builder.Default
    private String testEmailTarget = "info@imasagenciaaduanal.com";

    @Builder.Default
    private String status = "NUEVO"; // "NUEVO" | "EN_PROCESO" | "ATENDIDO"

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
