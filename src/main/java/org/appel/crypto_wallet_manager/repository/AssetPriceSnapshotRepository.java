package org.appel.crypto_wallet_manager.repository;

import org.appel.crypto_wallet_manager.domain.AssetPriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssetPriceSnapshotRepository extends JpaRepository<AssetPriceSnapshot, Long> {

    Optional<AssetPriceSnapshot> findFirstBySymbolIgnoreCaseOrderByCapturedAtDesc(String symbol);

    Optional<AssetPriceSnapshot> findFirstBySymbolIgnoreCaseAndCapturedAtLessThanEqualOrderByCapturedAtDesc(
            String symbol,
            Instant capturedAt
    );

    List<AssetPriceSnapshot> findByCapturedAtBetween(Instant from, Instant to);
}
