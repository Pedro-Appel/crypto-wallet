package org.appel.crypto_wallet_manager.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

    public static String convertEpochToISO(Long epoch) {
        return Instant.ofEpochSecond(epoch / 1000)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public static String convertISOToEpoch(Instant iso) {
        return String.valueOf(iso.toEpochMilli());
    }
}
