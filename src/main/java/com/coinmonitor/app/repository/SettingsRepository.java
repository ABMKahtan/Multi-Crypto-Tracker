package com.coinmonitor.app.repository;

import com.coinmonitor.app.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {

    Optional<Settings> findByCoinId(String coinId);

    Optional<Settings> findByCoinNumber(String coinNumber);
}
