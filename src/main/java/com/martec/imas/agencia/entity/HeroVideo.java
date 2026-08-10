package com.martec.imas.agencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hero_videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeroVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    private String badgeText;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (displayOrder == null) displayOrder = 1;
    }
}
