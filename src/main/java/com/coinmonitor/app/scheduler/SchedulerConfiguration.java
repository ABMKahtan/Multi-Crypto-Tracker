package com.coinmonitor.app.scheduler;

import com.coinmonitor.app.config.PriceTrackerConfigProps;
import com.coinmonitor.app.model.Settings;
import com.coinmonitor.app.service.PriceTrackerService;
import com.coinmonitor.app.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfiguration implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfiguration.class);
    private final PriceTrackerConfigProps priceTrackerConfigProps;
    private final PriceTrackerService priceTrackerService;
    private final SettingsService settingsService;
    @Value("${scheduler.frequency}")
    private Integer schedulerFrequency;
    private boolean firstRun = true;

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());

        taskRegistrar.addFixedDelayTask(new IntervalTask(
                () -> {
                    if (firstRun) {
                        LOGGER.debug("First run, trying to fetch settings");
                        List<Settings> settingsList = settingsService.loadSettings();
                        if (settingsList.size() == 0) {
                            LOGGER.debug("No existing settings found, saving default settings");
                            Map<String, PriceTrackerConfigProps.CoinDetail> coinMap = priceTrackerConfigProps.getCoinMap();

                            for(Map.Entry<String, PriceTrackerConfigProps.CoinDetail>  entry : coinMap.entrySet()){
                                PriceTrackerConfigProps.CoinDetail coinDetail = entry.getValue();
                                settingsService.save(
                                        entry.getKey(),
                                        coinDetail.getCoinId(),
                                        priceTrackerConfigProps.getCurrency(),
                                        priceTrackerConfigProps.getEmailToNotify(),
                                        coinDetail.getMinValue(),
                                        coinDetail.getMaxValue(),
                                        coinDetail.isEnabled()
                                );
                            }
                        }
                    }
                    try {
                        priceTrackerService.monitor();
                    } catch (Exception e) {
                        throw new RuntimeException("Unexpected error with monitoring service", e);
                    } finally {
                        firstRun = false;
                    }
                },
                schedulerFrequency
        ));
    }
}