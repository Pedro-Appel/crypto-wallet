package org.appel.crypto_wallet_manager.service;

import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;

import java.util.List;

public interface CoinAssetService {

    List<String> getAssetNames();

    void createAssetsSnapshot(AssetPriceResponse listOfAssets);
}
