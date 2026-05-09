package org.appel.crypto_wallet_manager.dto;

import java.util.List;

public record WalletPerformanceResponse(
        List<WalletPerformanceAssetResponse> assets,
        WalletPerformanceAssetResponse bestPerformingAsset,
        WalletPerformanceAssetResponse worstPerformingAsset
) {
}
