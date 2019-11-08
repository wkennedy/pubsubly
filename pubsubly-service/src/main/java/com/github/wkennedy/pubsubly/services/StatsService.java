package com.github.wkennedy.pubsubly.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class StatsService {
    private final Cache<String, MessageResource> messageCache;

    private final Map<String, Cache<String, MessageBundle>> cacheMap;

    private final MessageService messageService;

    public StatsService(Cache<String, MessageResource> messageCache, @Qualifier("cacheMap") Map<String, Cache<String, MessageBundle>> cacheMap, MessageService messageService) {
        this.messageCache = messageCache;
        this.cacheMap = cacheMap;
        this.messageService = messageService;
    }

    public Map<String, CountByDay> getCountsByDay() {
        Set<String> messageUUIDs = messageCache.asMap().keySet();
        Map<String, CountByDay> countByDayMap = new HashMap<>();
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            countByDayMap.put(dayOfWeek.name(), new CountByDay(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())));
        }

        for (String messageUUID : messageUUIDs) {
            MessageResource messageResource = messageCache.asMap().get(messageUUID);
            OffsetDateTime offsetDateTime = HeaderUtil.getOffsetDateTime(messageResource.getMessage().getHeaders());
            LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
            CountByDay countByDay = countByDayMap.get(localDateTime.getDayOfWeek().name());
            HourValue hourValue = countByDay.getHourValues().get(localDateTime.getHour());
            hourValue.setHour(localDateTime.getHour());

            hourValue.value = hourValue.value + 1;
        }

        return countByDayMap;
    }


//    TODO clean up these models
    public class CountByDay {
        String day;
        List<HourValue> hourValues = new ArrayList<>();
        Integer max = 0;

        public CountByDay(String day) {
            init();
            this.day = day;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public List<HourValue> getHourValues() {
            return hourValues;
        }

        public void setHourValues(List<HourValue> hourValues) {
            this.hourValues = hourValues;
            getMax();
        }

        private void init() {
            for(int i = 0; i < 24; i++) {
                hourValues.add(new HourValue(i));
            }
        }

        public Integer getMax() {
            for (HourValue hourValue : hourValues) {
                if(hourValue.value > max) {
                    max = hourValue.value;
                }
            }
            return max;
        }
    }

    public class HourValue {
        Integer hour;
        Integer value = 0;
        Integer index = 1;

        public HourValue(Integer hour) {
            this.hour = hour;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }

}
