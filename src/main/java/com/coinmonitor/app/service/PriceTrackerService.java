package com.coinmonitor.app.service;

import com.coinmonitor.app.config.PriceTrackerConfigProps;
import com.coinmonitor.app.model.CryptoPrice;
import com.coinmonitor.app.model.NotificationType;
import com.coinmonitor.app.model.Settings;
import com.coinmonitor.app.utils.LineChartEx;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceTrackerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceTrackerService.class);
    private static boolean lowEmailSent = false;
    private static boolean highEmailSent = false;

    private final CoinGeckoApiClient coinGeckoApiClient;
    private final PriceTrackerConfigProps priceTrackerConfigProps;
    private final EmailService emailService;
    private final SettingsService settingsService;
    private final CryptoPriceService cryptoPriceService;

    public void monitor() throws IOException {

        Map<String, PriceTrackerConfigProps.CoinDetail> coinMap = priceTrackerConfigProps.getCoinMap();

        String coinsToTrack = coinMap.values().stream().map(PriceTrackerConfigProps.CoinDetail::getCoinId).collect(Collectors.joining(","));

        String currency = priceTrackerConfigProps.getCurrency();
        String emailToNotify = priceTrackerConfigProps.getEmailToNotify();

        List<Settings> settingsList = settingsService.loadSettings();
        Map<String, PriceTrackerConfigProps.CoinDetail> finalCoinMap = new HashMap<>();
        if (settingsList.size() > 0) {
            for (Settings coin : settingsList) {
                PriceTrackerConfigProps.CoinDetail coinDetail = new PriceTrackerConfigProps.CoinDetail();
                coinDetail.setCoinId(coin.getCoinId());
                coinDetail.setMaxValue(coin.getMaxValue());
                coinDetail.setMinValue(coin.getMinValue());
                coinDetail.setEnabled(coin.getEnabled());
                finalCoinMap.put(coin.getCoinId(), coinDetail);
            }
        } else {
            finalCoinMap = coinMap;
        }

        Map<String, Map<String, Double>> apiResponse = coinGeckoApiClient.getPrice(coinsToTrack, currency);

        for (PriceTrackerConfigProps.CoinDetail coinDetail : finalCoinMap.values()) {
            if(coinDetail.isEnabled()){
                trackCoinPrice(currency, emailToNotify, apiResponse, coinDetail);
            }
        }


    }

    private void trackCoinPrice(String currency, String emailToNotify, Map<String, Map<String, Double>> apiResponse, PriceTrackerConfigProps.CoinDetail coinDetail) throws IOException {
        String coinId = coinDetail.getCoinId().toLowerCase();
        int minValue = coinDetail.getMinValue();
        int maxValue = coinDetail.getMaxValue();

        if (apiResponse.get(coinId) != null && apiResponse.get(coinId).get(currency) != null) {
            Integer currentPrice = apiResponse.get(coinId).get(currency).intValue();
            LOGGER.info("Current price for {} is {} {}", coinId, currentPrice, currency.toUpperCase());

            cryptoPriceService.save(coinId, currency.toUpperCase(), currentPrice);

            List<CryptoPrice> priceHistory = cryptoPriceService.findAllLast24Hours(coinId);
            Map<String, CryptoPrice> filteredMap = new HashMap<>();
            for(CryptoPrice cp : priceHistory){
                LocalDateTime ldt = LocalDateTime.ofInstant(cp.getDate(), ZoneId.systemDefault());
                String key = ldt.getDayOfMonth() + "_" + ldt.getMonthValue() + "_" + ldt.getYear() + "_" + ldt.getHour() + "_" + ldt.getMinute();
                filteredMap.put(key, cp);
            }

            JFreeChart chart = new LineChartEx().generateChart(coinId, filteredMap);
            Path tempFile = Files.createTempFile("chart_" + coinId, ".png");
            ChartUtils.saveChartAsPNG(tempFile.toFile(), chart, 1500, 850);

            if (currentPrice < minValue) {
                LOGGER.info("Current price {} is lower than minimum price {}", currentPrice, minValue);
                if (!highEmailSent) {
                    emailService.sendEmailNotification(
                            currentPrice,
                            coinId,
                            currency,
                            emailToNotify,
                            NotificationType.LOWER_THAN_MINIMUM,
                            tempFile.toFile()
                    );
                }
                highEmailSent = true;
                lowEmailSent = false;
            }

            if (currentPrice > maxValue) {
                LOGGER.info("Current price {} is higher than maximum price {}", currentPrice, maxValue);
                if (!lowEmailSent) {
                    emailService.sendEmailNotification(
                            currentPrice,
                            coinId,
                            currency,
                            emailToNotify,
                            NotificationType.HIGHER_THAN_MAX,
                            tempFile.toFile()
                    );
                    lowEmailSent = true;
                    highEmailSent = false;
                }
            }
        }
    }
}
