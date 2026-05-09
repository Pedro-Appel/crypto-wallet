package org.appel.crypto_wallet_manager.dto;

import java.math.BigDecimal;

public record WalletAssetResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal purchasePriceUsd,
        BigDecimal currentPriceUsd,
        BigDecimal currentValueUsd,
        String status
) {
}
