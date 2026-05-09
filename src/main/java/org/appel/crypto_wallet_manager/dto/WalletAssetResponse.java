package org.appel.crypto_wallet_manager.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletAssetResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal purchasePriceUsd,
        BigDecimal currentPriceUsd,
        BigDecimal currentValueUsd,
        Instant purchaseDate,
        String status
) {
}
