package org.appel.crypto_wallet_manager.dto;

import java.math.BigDecimal;

public record WalletPerformanceAssetResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal purchasePriceUsd,
        BigDecimal currentPriceUsd,
        BigDecimal percentageGrowth,
        BigDecimal usdGain
) {
}
