package org.appel.crypto_wallet_manager.service;

import org.appel.crypto_wallet_manager.service.impl.TimeConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Instant;

@Execution(ExecutionMode.CONCURRENT)
public class TimeConversionTest {
    @Test
    void convertToIso8601(){
        String iso = TimeConverter.convertEpochToISO(1767225600000L);
        assert iso.equals("2026-01-01T00:00:00Z");
    }
    @Test
    void convertToEpoch(){
        String epoch = TimeConverter.convertISOToEpoch(Instant.ofEpochSecond(1767225600000L));
        assert epoch.equals("1767225600000");
    }
}
