package org.appel.crypto_wallet_manager.service.impl;

import org.appel.crypto_wallet_manager.client.CoinCapClient;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.appel.crypto_wallet_manager.service.CoinAssetService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class AssetPriceScheduler {

    private final Logger logger = Logger.getLogger(AssetPriceScheduler.class.getName());

    private final CoinCapClient client;
    private final CoinAssetService coinAssetService;
    private final ThreadPoolTaskExecutor executorService;

    public AssetPriceScheduler(CoinCapClient client,
                               CoinAssetService coinAssetService,
                               @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor executorService) {
        this.client = client;
        this.coinAssetService = coinAssetService;
        this.executorService = executorService;
    }

    /// This is what I would do to minimize API Calls
    ///    String allDistinctNames =
    ///            coinAssetService.getAssetNames()
    ///                    .stream()
    ///                    .distinct()
    ///                    .collect(Collectors.joining(","));
    ///   if (allDistinctNames.isEmpty()) {
    ///        logger.warning("No assets registered in database");
    ///        return;
    ///    }
    ///    AssetPriceResponse listOfAssets = client.fetchAssetPrices(allDistinctNames, apiKey);
    ///
    ///    logger.fine("Updating asset price for assets in wallets: " + allDistinctNames);
    ///    coinAssetService.createAssetsSnapshot(listOfAssets);
    ///   logger.fine("Scheduled task to update asset prices...");
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRateString = "${coincap.api.asset-snapshot.fixed-rate}")
    public void asyncPriceExecutor() {
        logger.warning("Starting update");
        coinAssetService.getAssetNames()
                .stream()
                .distinct()
                .forEach(asset -> executorService.submit(() -> this.updatePrices(asset)));
    }

    private void updatePrices(String asset) {
        logger.fine("Updating asset price for asset: " + asset);
        AssetPriceResponse listOfAssets = client.fetchAssetPrices(asset);
        coinAssetService.createAssetsSnapshot(listOfAssets);
        logger.fine("Scheduled task to update asset prices...");
    }
}
