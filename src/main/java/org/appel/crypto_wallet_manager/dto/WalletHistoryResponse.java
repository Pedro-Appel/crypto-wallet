package org.appel.crypto_wallet_manager.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletHistoryResponse(
        Instant fromDate,
        BigDecimal totalWalletValueUsd
) {
}
