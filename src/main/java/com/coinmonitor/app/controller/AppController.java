package com.coinmonitor.app.controller;

import com.coinmonitor.app.dto.AppSettingsDTO;
import com.coinmonitor.app.model.Settings;
import com.coinmonitor.app.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/app")
@RequiredArgsConstructor
public class AppController {

    @Autowired
    private final SettingsService settingsService;


    @GetMapping("/settings")
    public ResponseEntity<?> getApplicationProperties() {
        List<Settings> settingsList = settingsService.loadSettings();
        Settings settings = settingsList.stream().findFirst().get();
        return ResponseEntity.ok(new AppSettingsDTO(settingsList, settings.getEmailToNotify(), settings.getCurrency()));
    }

    @GetMapping("/settings/update")
    public ResponseEntity<?> updateSettings(
            @RequestParam(value = "currency", required = false) String currency,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "coin1", required = false) String coinId,
            @RequestParam(value = "maxValue1", required = false) Integer maxValue,
            @RequestParam(value = "minValue1", required = false) Integer minValue,
            @RequestParam(value = "enabled1", required = false) Boolean enabled,
            @RequestParam(value = "coin2", required = false) String coinId2,
            @RequestParam(value = "maxValue2", required = false) Integer maxValue2,
            @RequestParam(value = "minValue2", required = false) Integer minValue2,
            @RequestParam(value = "enabled2", required = false) Boolean enabled2,
            @RequestParam(value = "coin3", required = false) String coinId3,
            @RequestParam(value = "maxValue3", required = false) Integer maxValue3,
            @RequestParam(value = "minValue3", required = false) Integer minValue3,
            @RequestParam(value = "enabled3", required = false) Boolean enabled3,
            @RequestParam(value = "coin4", required = false) String coinId4,
            @RequestParam(value = "maxValue4", required = false) Integer maxValue4,
            @RequestParam(value = "minValue4", required = false) Integer minValue4,
            @RequestParam(value = "enabled4", required = false) Boolean enabled4,
            @RequestParam(value = "coin5", required = false) String coinId5,
            @RequestParam(value = "maxValue5", required = false) Integer maxValue5,
            @RequestParam(value = "minValue5", required = false) Integer minValue5,
             @RequestParam(value = "enabled5", required = false) Boolean enabled5
    ) {
        if(coinId != null && !coinId.isEmpty()){
            settingsService.save(
                    "coin1",
                    coinId,
                    currency,
                    email,
                    minValue,
                    maxValue,
                    enabled
            );
        }

        if (coinId2 != null && !coinId2.isEmpty()) {
            settingsService.save(
                    "coin2",
                    coinId2,
                    currency,
                    email,
                    minValue2,
                    maxValue2,
                    enabled2
            );
        }
        if (coinId3 != null && !coinId3.isEmpty()) {
            settingsService.save(
                    "coin3",
                    coinId3,
                    currency,
                    email,
                    minValue3,
                    maxValue3,
                    enabled3
            );
        }
        if (coinId3 != null && !coinId3.isEmpty()) {
            settingsService.save(
                    "coin4",
                    coinId4,
                    currency,
                    email,
                    minValue4,
                    maxValue4,
                    enabled4
            );
        }
        if (coinId4 != null && !coinId4.isEmpty()) {
            settingsService.save(
                    "coin5",
                    coinId5,
                    currency,
                    email,
                    minValue5,
                    maxValue5,
                    enabled5
            );
        }

        return ResponseEntity.ok("Settings updated");
    }

}
