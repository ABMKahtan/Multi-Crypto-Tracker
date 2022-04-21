package com.coinmonitor.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "crypto")
@Getter
@Setter
public class PriceTrackerConfigProps {
    private String apiUrl;
    private String emailToNotify;
    private String currency;
    private Map<String, CoinDetail> coinMap;

    public static class CoinDetail {
        private String coinId;
        private int maxValue;
        private int minValue;
        private boolean enabled;

        public String getCoinId() {
            return coinId;
        }

        public void setCoinId(String coinId) {
            this.coinId = coinId;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        public int getMinValue() {
            return minValue;
        }

        public void setMinValue(int minValue) {
            this.minValue = minValue;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
