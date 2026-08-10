package com.martec.imas.agencia.Controllers;

import com.martec.imas.agencia.entity.SiteSetting;
import com.martec.imas.agencia.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SiteSettingController {

    private final SiteSettingRepository siteSettingRepository;

    @GetMapping("/api/v1/public/settings")
    public ResponseEntity<Map<String, String>> getAllSettings() {
        List<SiteSetting> settingsList = siteSettingRepository.findAll();
        Map<String, String> map = settingsList.stream()
                .collect(Collectors.toMap(SiteSetting::getSettingKey, SiteSetting::getSettingValue, (v1, v2) -> v2));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/api/v1/admin/settings")
    public ResponseEntity<SiteSetting> createOrUpdateSetting(@RequestBody SiteSetting setting) {
        SiteSetting saved = siteSettingRepository.save(setting);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/api/v1/admin/settings/batch")
    public ResponseEntity<Map<String, String>> updateBatchSettings(@RequestBody Map<String, String> settingsMap) {
        settingsMap.forEach((key, val) -> {
            SiteSetting setting = siteSettingRepository.findById(key)
                    .orElse(SiteSetting.builder().settingKey(key).build());
            setting.setSettingValue(val);
            siteSettingRepository.save(setting);
        });
        return getAllSettings();
    }
}
