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
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class AssetPriceFetchTest {

    AutoCloseable mockito;
    AssetPriceScheduler scheduler;
    @Mock
    private CoinCapClient coinCapClient;
    @Mock
    private CoinAssetRepository coinAssetRepository;
    @Mock
    private AssetPriceSnapshotRepository snapshotRepository;

    @BeforeEach
    void setUp(){
        mockito = MockitoAnnotations.openMocks(this);
        CoinAssetService coinAssetService = new CoinAssetServiceImpl(coinAssetRepository, snapshotRepository);
        scheduler = new AssetPriceScheduler(coinCapClient, "fake-api-key", coinAssetService);
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

        scheduler.onApplicationStarted();
        //Then should fetch the price for each distinct symbol in third party API
        BDDMockito.verify(coinCapClient).fetchAssetPrices(BDDMockito.anyString(), BDDMockito.anyString());
        //And  should save the prices for each distinct symbol in database
        BDDMockito.verify(snapshotRepository).saveAll(BDDMockito.anyList());
    }
}
