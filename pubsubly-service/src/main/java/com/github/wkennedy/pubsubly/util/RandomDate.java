package com.github.wkennedy.pubsubly.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class RandomDate {

    public static OffsetDateTime getDate() {
        Instant instant = Instant.ofEpochSecond(getRandomTimeBetweenTwoDates());
        LocalDateTime randomDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC-06:00"));
        return randomDate.atOffset(ZoneOffset.UTC);
    }

    private static long getRandomTimeBetweenTwoDates() {
        long beginTime = LocalDateTime.of(2013, 3, 1, 1, 1, 1).toEpochSecond(ZoneOffset.UTC);
        long endTime = LocalDateTime.of(2018, 3, 1, 1, 1, 1).toEpochSecond(ZoneOffset.UTC);
        long diff = endTime - beginTime + 1;
        return beginTime + (long) (Math.random() * diff);
    }

}