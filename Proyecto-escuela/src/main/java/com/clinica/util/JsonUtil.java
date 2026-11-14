package com.clinica.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson configurado para las clases del dominio que usan java.time.
 * Se utiliza, por ejemplo, para guardar el estado anterior de un dato clínico
 * en la tabla de auditoría.
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, tipo, ctx) -> src == null ? null
                            : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, tipo, ctx) -> src == null ? null
                            : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, tipo, ctx) -> src == null ? null
                            : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .serializeNulls()
            .create();

    private JsonUtil() {
    }

    public static String toJson(Object objeto) {
        return GSON.toJson(objeto);
    }
}
