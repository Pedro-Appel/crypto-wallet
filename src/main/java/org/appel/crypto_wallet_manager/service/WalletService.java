package org.appel.crypto_wallet_manager.service;

import org.appel.crypto_wallet_manager.dto.*;
import org.appel.crypto_wallet_manager.dto.request.AddCoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.WalletCreateRequest;

import java.time.Instant;
import java.util.List;

public interface WalletService {

    WalletSummaryResponse getWalletSummary(Long id);

    List<WalletAssetResponse> getWalletAssets(Long id);

    WalletHistoryResponse getWalletHistory(Instant fromDate, Long id);

    WalletPerformanceResponse getWalletPerformance(Long id);

    WalletCreatedResponse createWallet(WalletCreateRequest request);

    void addAssetToWallet(Long walletId, AddCoinAssetRequest request);

}
