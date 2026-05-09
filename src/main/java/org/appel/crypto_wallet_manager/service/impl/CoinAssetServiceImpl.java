package org.appel.crypto_wallet_manager.service.impl;

import jakarta.transaction.Transactional;
import org.appel.crypto_wallet_manager.domain.AssetPriceSnapshot;
import org.appel.crypto_wallet_manager.dto.AssetData;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.appel.crypto_wallet_manager.repository.AssetPriceSnapshotRepository;
import org.appel.crypto_wallet_manager.repository.CoinAssetRepository;
import org.appel.crypto_wallet_manager.service.CoinAssetService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CoinAssetServiceImpl implements CoinAssetService {

    private final CoinAssetRepository coinAssetRepository;
    private final AssetPriceSnapshotRepository snapshotRepository;
    Logger log = Logger.getLogger(CoinAssetServiceImpl.class.getName());

    public CoinAssetServiceImpl(CoinAssetRepository coinAssetRepository,
                                AssetPriceSnapshotRepository snapshotRepository) {
        this.coinAssetRepository = coinAssetRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    @Transactional
    @Cacheable("assetNames")
    public List<String> getAssetNames() {
        return coinAssetRepository.fetchDifferentNames();
    }

    @Override
    public void createAssetsSnapshot(AssetPriceResponse listOfAssets) {
        List<AssetPriceSnapshot> assetPrices = listOfAssets.data().stream()
                .map(asset -> toCoinAsset(listOfAssets.timestamp(), asset))
                .toList();
        snapshotRepository.saveAll(assetPrices);
    }

    private AssetPriceSnapshot toCoinAsset(Long timestamp, AssetData asset) {
        String epoch = TimeConverter.convertEpochToISO(timestamp);
        Instant parse = Instant.parse(epoch);
        log.finer("Captured: "+ epoch+", Parsed: "+ parse);

        return new AssetPriceSnapshot(
                asset.symbol(),
                asset.name(),
                parse,
                new BigDecimal(asset.priceUsd())
        );
    }
}
