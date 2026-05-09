package org.appel.crypto_wallet_manager.service;

import org.appel.crypto_wallet_manager.TestUtils;
import org.appel.crypto_wallet_manager.client.CoinCapClient;
import org.appel.crypto_wallet_manager.repository.AssetPriceSnapshotRepository;
import org.appel.crypto_wallet_manager.repository.CoinAssetRepository;
import org.appel.crypto_wallet_manager.service.impl.AssetPriceScheduler;
import org.appel.crypto_wallet_manager.service.impl.CoinAssetServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
public class AssetPriceFetchTest {

    AutoCloseable mockito;
    AssetPriceScheduler scheduler;
    @Mock
    private CoinCapClient coinCapClient;
    @Mock
    private CoinAssetRepository coinAssetRepository;
    @Mock
    private AssetPriceSnapshotRepository snapshotRepository;
    private final ThreadPoolTaskExecutor executorService = new ThreadPoolTaskExecutor();

    @BeforeEach
    void setUp() {
        mockito = MockitoAnnotations.openMocks(this);
        CoinAssetService coinAssetService = new CoinAssetServiceImpl(coinAssetRepository, snapshotRepository);
        executorService.setCorePoolSize(3);
        executorService.setAwaitTerminationSeconds(3);
        executorService.setWaitForTasksToCompleteOnShutdown(true);
        executorService.initialize();
        scheduler = new AssetPriceScheduler(coinCapClient, "fake-api-key", coinAssetService, executorService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockito.close();
    }

    @Test
    void fetchPriceAndSaveSnapshot() {
        //Given there is a wallet saved in the BD
        BDDMockito.given(coinAssetRepository.fetchDifferentNames())
                .willReturn(List.of("bitcoin", "ethereum", "cardano"));
        //And the external API response OK
        BDDMockito.given(coinCapClient.fetchAssetPrices(BDDMockito.anyString(), BDDMockito.anyString()))
                .willReturn(TestUtils.getCoinCapResponseAsObject());
        //When the schedule hit

        scheduler.asyncPriceExecutor();
        // Wait for all tasks to complete
        executorService.shutdown();
        //Then should fetch the price for each distinct symbol in third party API
        BDDMockito.verify(coinCapClient, Mockito.times(3)).fetchAssetPrices(BDDMockito.anyString(), BDDMockito.anyString());
        //And  should save the prices for each distinct symbol in database
        BDDMockito.verify(snapshotRepository, Mockito.times(3)).saveAll(BDDMockito.anyList());
    }
}
