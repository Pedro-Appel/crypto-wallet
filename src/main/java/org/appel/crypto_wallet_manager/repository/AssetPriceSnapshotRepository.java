package org.appel.crypto_wallet_manager.repository;

import org.appel.crypto_wallet_manager.domain.AssetPriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AssetPriceSnapshotRepository extends JpaRepository<AssetPriceSnapshot, Long> {

    List<AssetPriceSnapshot> findBySymbolIgnoreCaseOrderByCapturedAtDesc(String symbol);

    List<AssetPriceSnapshot> findByCapturedAtBetween(Instant from, Instant to);
}
