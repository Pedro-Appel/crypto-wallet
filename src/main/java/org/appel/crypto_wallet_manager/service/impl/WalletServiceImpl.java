package org.appel.crypto_wallet_manager.service.impl;

import org.appel.crypto_wallet_manager.client.CoinCapClient;
import org.appel.crypto_wallet_manager.domain.AssetPriceSnapshot;
import org.appel.crypto_wallet_manager.domain.CoinAsset;
import org.appel.crypto_wallet_manager.domain.Wallet;
import org.appel.crypto_wallet_manager.dto.*;
import org.appel.crypto_wallet_manager.dto.request.AddCoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.CoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.WalletCreateRequest;
import org.appel.crypto_wallet_manager.exception.NotFoundException;
import org.appel.crypto_wallet_manager.exception.ServiceNotAvailable;
import org.appel.crypto_wallet_manager.repository.AssetPriceSnapshotRepository;
import org.appel.crypto_wallet_manager.repository.WalletRepository;
import org.appel.crypto_wallet_manager.service.WalletService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final int MONEY_SCALE = 8;
    private static final int PERCENT_SCALE = 2;
    public static final String HISTORY_CACHE_KEY = "#p1-p2";

    private final String apiKey;
    private final CoinCapClient coinCapClient;
    private final WalletRepository walletRepository;
    private final AssetPriceSnapshotRepository assetPriceSnapshotRepository;
    private final Logger log = Logger.getLogger(WalletServiceImpl.class.getName());

    public WalletServiceImpl(
            WalletRepository walletRepository,
            AssetPriceSnapshotRepository assetPriceSnapshotRepository,
            CoinCapClient coinCapClient,
            @Value("${coincap.api.apiKey}") String apiKey) {
        this.walletRepository = walletRepository;
        this.assetPriceSnapshotRepository = assetPriceSnapshotRepository;
        this.coinCapClient = coinCapClient;
        this.apiKey = apiKey;
    }

    private static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    @Cacheable(cacheNames = "walletSummary", key = "#id")
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

    @Cacheable(cacheNames = "walletHistory", key = HISTORY_CACHE_KEY)
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

    @Cacheable(cacheNames = "walletPerformance", key = "#id")
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
                .orElseThrow(() -> new NotFoundException("Wallet not found"));
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
        try {
            List<AssetData> data = coinCapClient.searchAssetName(request.symbol(), 1, apiKey)
                    .data();
            if (data.isEmpty()) {
                throw new NotFoundException("Asset not found: " + request.symbol());
            }

            return new CoinAsset(
                    request.symbol(),
                    data.getFirst().name(),
                    request.quantity(),
                    request.purchasePrice(),
                    request.purchaseDate()
            );
        } catch (Exception e) {
            throw new ServiceNotAvailable("Service is not available, not possible to finalize call");
        }
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
                .map(WalletServiceImpl::scaleMoney)
                .orElse(returnDefault(asset));
    }

    private @NonNull BigDecimal returnDefault(CoinAsset asset) {
        log.warning("No snapshot price found, defaulting to normal multiplication");
        return asset.getPurchasePrice().multiply(asset.getQuantity());
    }

    private Optional<AssetPriceSnapshot> currentPriceFor(String symbol) {
        try {
            return assetPriceSnapshotRepository.findFirstBySymbolIgnoreCaseOrderByCapturedAtDesc(symbol);
        } catch (Exception e) {
            throw new ServiceNotAvailable("Service is not available, not possible to finalize call");
        }
    }

    private Optional<BigDecimal> historicalPriceFor(String symbol, Instant fromDate) {
        try {
            return assetPriceSnapshotRepository
                    .findFirstBySymbolIgnoreCaseAndCapturedAtLessThanEqualOrderByCapturedAtDesc(symbol, fromDate)
                    .map(AssetPriceSnapshot::getPriceUsd);
        } catch (Exception e) {
            throw new ServiceNotAvailable("Service is not available, not possible to finalize call");
        }
    }
}
