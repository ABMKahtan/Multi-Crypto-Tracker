package com.coinmonitor.app.service;

import com.coinmonitor.app.model.Settings;
import com.coinmonitor.app.repository.SettingsRepository;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final CoinGeckoApiClient coinGeckoApiClient;

    public List<Settings> loadSettings() {
        return settingsRepository.findAll();
    }

    public void save(Settings settings) {
        settingsRepository.save(settings);
    }

    public void save(String coinNumber, String coinId, String currency, String email, int minValue, int maxValue, boolean enabled) {

        Optional<CoinList> coinItemOpt = coinGeckoApiClient.getCoinList().stream().filter(coinItem -> coinItem.getId().equalsIgnoreCase(coinId)).findFirst();
        boolean supportsCurrency = coinGeckoApiClient.getSupportedVsCurrencies().contains(currency.toLowerCase());

        Settings settings = Settings.builder()
                .coinNumber(coinNumber)
                .minValue(minValue)
                .maxValue(maxValue)
                .emailToNotify(email)
                .enabled(enabled)
                .build();

        if (coinItemOpt.isPresent()) {
            settings.setCoinId(coinId.toUpperCase());
        }

        if(supportsCurrency){
            settings.setCurrency(currency);
        }

        Optional<Settings> settingsOpt = settingsRepository.findByCoinNumber(coinNumber);

        settingsOpt.ifPresentOrElse(
                existingSettings -> {
                    existingSettings.setCoinNumber(settings.getCoinNumber());
                    existingSettings.setCoinId(settings.getCoinId());
                    existingSettings.setCurrency(settings.getCurrency());
                    existingSettings.setEmailToNotify(settings.getEmailToNotify());
                    existingSettings.setMaxValue(settings.getMaxValue());
                    existingSettings.setMinValue(settings.getMinValue());
                    existingSettings.setEnabled(settings.getEnabled());
                    settingsRepository.save(existingSettings);
                },
                () -> {
                    settingsRepository.save(settings);
                }
        );
    }
}
