package com.yandex.app.http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration != null ? duration.toMinutes() : null);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        Long minutes = jsonReader.nextLong();
        return minutes != null ? Duration.ofMinutes(minutes) : null;
    }
}
