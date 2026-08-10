package com.martec.imas.agencia.repository;

import com.martec.imas.agencia.entity.HeroVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HeroVideoRepository extends JpaRepository<HeroVideo, Long> {
    List<HeroVideo> findByIsActiveTrueOrderByDisplayOrderAsc();
}
