package me.loper.storage.nosql.redis.serializer;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonByteBufferSerializer<T> {

    private final Charset charset = StandardCharsets.UTF_8;

    private final Class<T> type;
    private final Gson gson;

    public JsonByteBufferSerializer(Class<T> objectType) {
        this.gson = new Gson();
        this.type = objectType;
    }

    public JsonByteBufferSerializer(Class<T> objectType, Gson gson) {
        this.gson = gson;
        this.type = objectType;
    }

    public T decode(ByteBuffer bytes) {
        String rawJson = Unpooled.wrappedBuffer(bytes).toString(this.charset);

        return this.gson.fromJson(rawJson, this.type);
    }

    public ByteBuffer encode(T value) {
        return ByteBuffer.wrap(new Gson().toJson(value).getBytes(this.charset));
    }
}
