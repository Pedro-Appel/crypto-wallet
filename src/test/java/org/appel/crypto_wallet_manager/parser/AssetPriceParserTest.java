package org.appel.crypto_wallet_manager.parser;

import org.appel.crypto_wallet_manager.TestUtils;
import org.appel.crypto_wallet_manager.dto.AssetData;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AssetPriceParserTest {


    @Test
    void apiResponseParser(){
        AssetPriceResponse assetPriceResponse = TestUtils.getCoinCapResponseAsObject();

        List<AssetData> data = assetPriceResponse.data();
        Assertions.assertNotNull(data);
        AssetData first = data.getFirst();
        Assertions.assertNotNull(first);
        Assertions.assertEquals("bitcoin", first.id());
        Assertions.assertEquals("Bitcoin", first.name());
        Assertions.assertEquals("BTC", first.symbol());
        Assertions.assertEquals("80399.570000000006984919", first.priceUsd());
    }


}
