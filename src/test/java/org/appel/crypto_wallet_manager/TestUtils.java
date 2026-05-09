package org.appel.crypto_wallet_manager;

import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import tools.jackson.databind.ObjectMapper;

public class TestUtils {
    private final static ObjectMapper JACKSON = new ObjectMapper();

    public static String getCoinCapResponseAsString() {
        return """
                {
                    "data": [
                        {
                            "changePercent24Hr": "0.2819884727482884",
                            "explorer": "https://blockchain.info/",
                            "id": "bitcoin",
                            "marketCapUsd": "1610151816845.470214843750000000",
                            "maxSupply": "21000000.000000000000000000",
                            "name": "Bitcoin",
                            "priceUsd": "80399.570000000006984919",
                            "rank": "1",
                            "supply": "20026871.000000000000000000",
                            "symbol": "BTC",
                            "tokens": {},
                            "volumeUsd24Hr": "19641391949.020343780517578125",
                            "vwap24Hr": "80210.72481609907"
                        }
                    ],
                    "timestamp": 1778331718861
                }
                """;
    }

    public static AssetPriceResponse getCoinCapResponseAsObject() {
        return JACKSON.readValue(TestUtils.getCoinCapResponseAsString(), AssetPriceResponse.class);
    }
}
