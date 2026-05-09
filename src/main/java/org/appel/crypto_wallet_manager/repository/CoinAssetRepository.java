package org.appel.crypto_wallet_manager.repository;

import org.appel.crypto_wallet_manager.domain.CoinAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CoinAssetRepository extends JpaRepository<CoinAsset, Long> {

    @Query("SELECT name FROM CoinAsset")
    List<String> fetchDifferentNames();
}
