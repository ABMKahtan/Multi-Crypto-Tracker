package com.coinmonitor.app.dto;


import com.coinmonitor.app.model.Settings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class AppSettingsDTO {
    List<SettingsDTO> settings;
    private String emailToNotify;
    private String currency;


    public AppSettingsDTO(List<Settings> settingsList, String email, String currencyToTrack) {
        currency = currencyToTrack;
        emailToNotify = email;
        List<SettingsDTO> settingsDtoList = new ArrayList<>();
        for (Settings settings : settingsList) {
            settingsDtoList.add(new SettingsDTO(settings.getCoinId().toUpperCase(), settings.getCoinNumber(), settings.getMaxValue(), settings.getMinValue(), settings.getEnabled()));
        }
        settings = settingsDtoList;
    }
}
