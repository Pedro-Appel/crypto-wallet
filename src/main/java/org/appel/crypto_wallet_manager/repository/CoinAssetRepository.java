package org.appel.crypto_wallet_manager.repository;

import org.appel.crypto_wallet_manager.domain.CoinAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinAssetRepository extends JpaRepository<CoinAsset, Long> {

    List<CoinAsset> findBySymbolIgnoreCase(String symbol);
}
