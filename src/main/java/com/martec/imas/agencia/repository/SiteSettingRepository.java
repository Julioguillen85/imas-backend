package com.martec.imas.agencia.repository;

import com.martec.imas.agencia.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {
    List<SiteSetting> findBySection(String section);
}
