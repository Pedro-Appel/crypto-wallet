package org.appel.crypto_wallet_manager.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.appel.crypto_wallet_manager.dto.*;
import org.appel.crypto_wallet_manager.dto.request.AddCoinAssetRequest;
import org.appel.crypto_wallet_manager.dto.request.WalletCreateRequest;
import org.appel.crypto_wallet_manager.service.WalletService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @RateLimiter(name = "wallet")
    @GetMapping("/wallet/{id}")
    public WalletSummaryResponse getWalletSummary(@PathVariable Long id) {
        return walletService.getWalletSummary(id);
    }

    @RateLimiter(name = "wallet")
    @GetMapping("/wallet/{id}/assets")
    public List<WalletAssetResponse> getWalletAssets(@PathVariable Long id) {
        return walletService.getWalletAssets(id);
    }

    @RateLimiter(name = "wallet")
    @GetMapping("/wallet/{id}/history")
    public ResponseEntity<WalletHistoryResponse> getWalletHistory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate) {
        WalletHistoryResponse response = walletService.getWalletHistory(fromDate, id);
        return ResponseEntity.ok(response);
    }

    @RateLimiter(name = "wallet")
    @GetMapping("/wallet/{id}/performance")
    public WalletPerformanceResponse getWalletPerformance(@PathVariable Long id) {
        return walletService.getWalletPerformance(id);
    }

    @RateLimiter(name = "wallet")
    @PostMapping("/wallet")
    public ResponseEntity<WalletCreatedResponse> createWallet(@RequestBody WalletCreateRequest request) {
        WalletCreatedResponse response = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RateLimiter(name = "wallet")
    @PostMapping("/wallet/{walletId}/assets")
    public ResponseEntity<Void> addAssetToWallet(
            @PathVariable Long walletId,
            @RequestBody AddCoinAssetRequest request) {
        walletService.addAssetToWallet(walletId, request);
        return ResponseEntity.ok().build();
    }
}
