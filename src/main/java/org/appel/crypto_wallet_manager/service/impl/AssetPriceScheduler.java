package org.appel.crypto_wallet_manager.service.impl;

import org.appel.crypto_wallet_manager.client.CoinCapClient;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.appel.crypto_wallet_manager.service.CoinAssetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AssetPriceScheduler {

    private final Logger logger = Logger.getLogger(AssetPriceScheduler.class.getName());

    private final String apiKey;
    private final CoinCapClient client;
    private final CoinAssetService coinAssetService;

    public AssetPriceScheduler(CoinCapClient client,
                               @Value("${coincap.api.apiKey}") String apiKey,
                               CoinAssetService coinAssetService) {
        this.client = client;
        this.apiKey = apiKey;
        this.coinAssetService = coinAssetService;
    }

    @Scheduled(fixedRateString = "${coincap.api.asset-snapshot.fixed-rate}", timeUnit = TimeUnit.SECONDS) // Runs every 30s
    void updateAssetPrice(){
        String allDistinctNames = coinAssetService.getAssetNames()
                .stream()
                .distinct()
                .collect(Collectors.joining(","));
        if(allDistinctNames.isEmpty()) {
            logger.warning("No assets registered in database");
            return;
        }
        AssetPriceResponse listOfAssets = client.fetchAssetPrices(allDistinctNames, apiKey);
        logger.finer("Updating asset price for assets in wallets: " + allDistinctNames);
        coinAssetService.createAssetsSnapshot(listOfAssets);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        try {
            logger.config("Application started, fetching initial asset prices...");
            updateAssetPrice();  // Run once on application startup
        } catch (Exception e) {
            logger.warning("Failed to fetch asset prices on startup: " + e.getMessage());
        }
    }
}
