package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.entity.HeroVideo;
import com.martec.imas.agencia.repository.HeroVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HeroVideoController {

    private final HeroVideoRepository heroVideoRepository;

    @GetMapping("/api/v1/public/hero")
    public ResponseEntity<List<HeroVideo>> getPublicHeroVideos() {
        List<HeroVideo> videos = heroVideoRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        if (videos.isEmpty()) {
            videos = heroVideoRepository.findAll();
        }
        return ResponseEntity.ok(videos);
    }

    @PostMapping("/api/v1/admin/hero")
    public ResponseEntity<HeroVideo> createHeroVideo(@RequestBody HeroVideo video) {
        if (video.getIsActive() == null) video.setIsActive(true);
        if (video.getDisplayOrder() == null) video.setDisplayOrder((int) heroVideoRepository.count() + 1);
        HeroVideo saved = heroVideoRepository.save(video);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/api/v1/admin/hero/{id}")
    public ResponseEntity<HeroVideo> updateHeroVideo(@PathVariable Long id, @RequestBody HeroVideo videoDetails) {
        HeroVideo video = heroVideoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeroVideo not found with id: " + id));

        if (videoDetails.getTitle() != null) video.setTitle(videoDetails.getTitle());
        if (videoDetails.getSubtitle() != null) video.setSubtitle(videoDetails.getSubtitle());
        if (videoDetails.getVideoUrl() != null) video.setVideoUrl(videoDetails.getVideoUrl());
        if (videoDetails.getBadgeText() != null) video.setBadgeText(videoDetails.getBadgeText());
        if (videoDetails.getDisplayOrder() != null) video.setDisplayOrder(videoDetails.getDisplayOrder());
        if (videoDetails.getIsActive() != null) video.setIsActive(videoDetails.getIsActive());

        HeroVideo updated = heroVideoRepository.save(video);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/v1/admin/hero/{id}")
    public ResponseEntity<Void> deleteHeroVideo(@PathVariable Long id) {
        heroVideoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
