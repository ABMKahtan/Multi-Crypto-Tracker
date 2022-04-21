package com.coinmonitor.app.repository;

import com.coinmonitor.app.model.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Integer> {

    @Query("select c from CryptoPrice c where c.date >= :oneDayAgoDate AND c.coin = :coinId")
    List<CryptoPrice> findAllLast24Hours(@Param("oneDayAgoDate") Instant oneDayAgoDate, @Param("coinId") String coinId);
}
