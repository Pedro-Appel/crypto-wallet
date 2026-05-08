package org.appel.crypto_wallet_manager.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    public static String convertEpochToISO(String epoch) {
        return Instant.ofEpochSecond(Long.parseLong(epoch)/1000)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
