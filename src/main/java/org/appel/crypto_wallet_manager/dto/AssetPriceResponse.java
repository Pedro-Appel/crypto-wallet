package org.appel.crypto_wallet_manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetPriceResponse(
        Long timestamp,
        List<AssetData> data
) {}