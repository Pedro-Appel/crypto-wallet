package org.appel.crypto_wallet_manager.service;

import org.appel.crypto_wallet_manager.domain.AssetPriceSnapshot;
import org.appel.crypto_wallet_manager.domain.CoinAsset;
import org.appel.crypto_wallet_manager.domain.Wallet;
import org.appel.crypto_wallet_manager.dto.WalletAssetResponse;
import org.appel.crypto_wallet_manager.dto.WalletCreatedResponse;
import org.appel.crypto_wallet_manager.dto.WalletHistoryResponse;
import org.appel.crypto_wallet_manager.dto.WalletPerformanceAssetResponse;
import org.appel.crypto_wallet_manager.dto.WalletPerformanceResponse;
import org.appel.crypto_wallet_manager.dto.WalletSummaryResponse;
import org.appel.crypto_wallet_manager.dto.request.AddCoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.CoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.WalletCreateRequest;
import org.appel.crypto_wallet_manager.exception.NotFoundException;
import org.appel.crypto_wallet_manager.repository.AssetPriceSnapshotRepository;
import org.appel.crypto_wallet_manager.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletService {

    private static final int MONEY_SCALE = 8;
    private static final int PERCENT_SCALE = 2;

    private final WalletRepository walletRepository;
    private final AssetPriceSnapshotRepository assetPriceSnapshotRepository;

    public WalletService(
            WalletRepository walletRepository,
            AssetPriceSnapshotRepository assetPriceSnapshotRepository
    ) {
        this.walletRepository = walletRepository;
        this.assetPriceSnapshotRepository = assetPriceSnapshotRepository;
    }

    public WalletSummaryResponse getWalletSummary(Long id) {
        Wallet wallet = getSingleWallet(id);
        BigDecimal totalValue = wallet.getAssets().stream()
                .map(this::currentAssetValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new WalletSummaryResponse(scaleMoney(totalValue));
    }

    public List<WalletAssetResponse> getWalletAssets(Long id) {
        Wallet wallet = getSingleWallet(id);
        return wallet.getAssets().stream()
                .map(this::toWalletAssetResponse)
                .toList();
    }

    public WalletHistoryResponse getWalletHistory(Instant fromDate, Long id) {
        Wallet wallet = getSingleWallet(id);

        List<BigDecimal> values = wallet.getAssets().stream()
                .map(asset -> historicalPriceFor(asset.getSymbol(), fromDate)
                        .map(price -> price.multiply(asset.getQuantity()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (values.isEmpty()) {
            throw new NotFoundException("No history found");
        }

        BigDecimal totalValue = values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new WalletHistoryResponse(fromDate, scaleMoney(totalValue));
    }

    public WalletPerformanceResponse getWalletPerformance(Long id) {
        Wallet wallet = getSingleWallet(id);
        List<WalletPerformanceAssetResponse> assets = wallet.getAssets().stream()
                .collect(Collectors.groupingBy(
                        CoinAsset::getSymbol,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(this::toWalletPerformanceAssetResponse)
                .filter(response -> response.currentPriceUsd() != null)
                .toList();

        WalletPerformanceAssetResponse best = assets.stream()
                .max(Comparator.comparing(WalletPerformanceAssetResponse::percentageGrowth))
                .orElse(null);
        WalletPerformanceAssetResponse worst = assets.stream()
                .min(Comparator.comparing(WalletPerformanceAssetResponse::percentageGrowth))
                .orElse(null);

        return new WalletPerformanceResponse(assets, best, worst);
    }

    public WalletCreatedResponse createWallet(WalletCreateRequest request) {
        Wallet wallet = new Wallet();
        for (CoinAssetRequest assetRequest : request.assets()) {
            wallet.addAsset(toCoinAsset(assetRequest));
        }
        Wallet saved = walletRepository.save(wallet);
        return new WalletCreatedResponse(saved.getId());
    }

    public void addAssetToWallet(Long walletId, AddCoinAssetRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Wallet not found"));
        wallet.addAsset(toCoinAsset(new CoinAssetRequest(
                request.symbol(),
                request.quantity(),
                request.purchasePrice(),
                request.purchaseDate()
        )));
        walletRepository.save(wallet);
    }

    private Wallet getSingleWallet(Long id) {
        return walletRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Wallet not found"));
    }

    private CoinAsset toCoinAsset(CoinAssetRequest request) {
        return new CoinAsset(
                request.symbol(),
                request.quantity(),
                request.purchasePrice(),
                request.purchaseDate()
        );
    }

    private WalletAssetResponse toWalletAssetResponse(CoinAsset asset) {
        Optional<AssetPriceSnapshot> currentPrice = currentPriceFor(asset.getSymbol());
        BigDecimal currentPriceUsd = currentPrice.map(AssetPriceSnapshot::getPriceUsd).orElse(null);
        BigDecimal currentValueUsd = currentPriceUsd == null
                ? null
                : scaleMoney(currentPriceUsd.multiply(asset.getQuantity()));

        return new WalletAssetResponse(
                asset.getSymbol(),
                asset.getQuantity(),
                asset.getPurchasePrice(),
                currentPriceUsd,
                currentValueUsd,
                currentPriceUsd == null ? "UNAVAILABLE" : "AVAILABLE"
        );
    }

    private WalletPerformanceAssetResponse toWalletPerformanceAssetResponse(List<CoinAsset> assets) {
        CoinAsset representative = assets.getFirst();
        AssetPriceSnapshot currentPrice = currentPriceFor(representative.getSymbol()).orElse(null);
        if (currentPrice == null) {
            return new WalletPerformanceAssetResponse(
                    representative.getSymbol(),
                    totalQuantity(assets),
                    weightedAveragePurchasePrice(assets),
                    null,
                    null,
                    null
            );
        }

        BigDecimal currentPriceUsd = currentPrice.getPriceUsd();
        BigDecimal purchasePriceUsd = weightedAveragePurchasePrice(assets);
        BigDecimal totalQuantity = totalQuantity(assets);
        BigDecimal totalCost = totalCost(assets);
        BigDecimal priceDelta = currentPriceUsd.subtract(purchasePriceUsd);
        BigDecimal percentageGrowth = priceDelta
                .divide(purchasePriceUsd, PERCENT_SCALE + 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENT_SCALE, RoundingMode.HALF_UP);
        BigDecimal usdGain = scaleMoney(currentPriceUsd.multiply(totalQuantity).subtract(totalCost));

        return new WalletPerformanceAssetResponse(
                representative.getSymbol(),
                totalQuantity,
                purchasePriceUsd,
                currentPriceUsd,
                percentageGrowth,
                usdGain
        );
    }

    private BigDecimal weightedAveragePurchasePrice(List<CoinAsset> assets) {
        BigDecimal totalQuantity = totalQuantity(assets);
        if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return scaleMoney(totalCost(assets).divide(totalQuantity, MONEY_SCALE, RoundingMode.HALF_UP));
    }

    private BigDecimal totalQuantity(List<CoinAsset> assets) {
        return assets.stream()
                .map(CoinAsset::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalCost(List<CoinAsset> assets) {
        return assets.stream()
                .map(asset -> asset.getQuantity().multiply(asset.getPurchasePrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal currentAssetValue(CoinAsset asset) {
        return currentPriceFor(asset.getSymbol())
                .map(snapshot -> snapshot.getPriceUsd().multiply(asset.getQuantity()))
                .map(WalletService::scaleMoney)
                .orElse(asset.getPurchasePrice().multiply(asset.getQuantity()));
    }

    private Optional<AssetPriceSnapshot> currentPriceFor(String symbol) {
        return assetPriceSnapshotRepository.findFirstBySymbolIgnoreCaseOrderByCapturedAtDesc(symbol);
    }

    private Optional<BigDecimal> historicalPriceFor(String symbol, Instant fromDate) {
        return assetPriceSnapshotRepository
                .findFirstBySymbolIgnoreCaseAndCapturedAtLessThanEqualOrderByCapturedAtDesc(symbol, fromDate)
                .map(AssetPriceSnapshot::getPriceUsd);
    }

    private static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
