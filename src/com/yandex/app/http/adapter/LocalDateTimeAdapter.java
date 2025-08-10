package com.yandex.app.http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime != null
                ? localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return value != null
                ? LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;
    }
}