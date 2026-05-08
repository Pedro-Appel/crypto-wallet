package org.appel.crypto_wallet_manager.service;

import org.junit.jupiter.api.Test;

public class TimeConversionTest {
    @Test
    void convertToIso8601(){
        String iso = TimeConverter.convertEpochToISO("1767225600000");
        System.out.println(iso);
        assert iso.equals("2026-01-01T00:00:00Z");
    }
}
