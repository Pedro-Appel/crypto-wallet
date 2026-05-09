package org.appel.crypto_wallet_manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetData(
        String id,
        String rank,
        String symbol,
        String name,
        String priceUsd
) {}