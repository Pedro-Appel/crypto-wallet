package org.appel.crypto_wallet_manager.dto.request;

import java.util.List;

public record WalletCreateRequest(List<CoinAssetRequest> assets) {
}
