package com.skillswap.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                                src == null ? null : context.serialize(src.format(FORMATTER)))
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                json == null || json.isJsonNull()
                                        ? null
                                        : LocalDateTime.parse(json.getAsString(), FORMATTER))
                .create();
    }
}