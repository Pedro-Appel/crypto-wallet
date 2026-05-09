package org.appel.crypto_wallet_manager.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

public record CoinAssetRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal purchasePrice,
        Instant purchaseDate
) {
}
