package org.appel.crypto_wallet_manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "asset_price_snapshots",
        indexes = {
                @Index(name = "idx_asset_price_symbol_captured_at", columnList = "symbol,captured_at")
        }
)
public class AssetPriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String symbol;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @Column(name = "price_usd", nullable = false, precision = 2, scale = 8)
    private BigDecimal priceUsd;

    protected AssetPriceSnapshot() {
    }

    public AssetPriceSnapshot(String symbol, Instant capturedAt, BigDecimal priceUsd) {
        this.symbol = symbol;
        this.capturedAt = capturedAt;
        this.priceUsd = priceUsd;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }
}
