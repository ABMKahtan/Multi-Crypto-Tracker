package com.coinmonitor.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettingsDTO {
    private String crypto;
    private String coinNumber;
    private int maxValue;
    private int minValue;
    private boolean enabled;
}
