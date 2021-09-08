package org.subspark.utils;

import java.nio.charset.StandardCharsets;

public class SerializationUtils {
    public static byte[] serialize(Object obj) {
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        } else {
            // Try to use toString() to serialize the object
            return obj.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
