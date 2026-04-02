package com.example.foodorder.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.regex.*;

/**
 * Lightweight hand-written JSON utility.
 * 
 * Demonstrates:
 *   - Java Reflection to walk getter methods on any POJO
 *   - Recursive serialization of nested objects and lists
 *   - Simple regex-based parsing for flat request DTOs
 *
 * No third-party libraries are used.
 */
public final class JsonUtil {

    private JsonUtil() { /* utility class — no instances */ }

    // ─────────────────────────────────────────────────────
    // SERIALIZATION  (Object → JSON string)
    // ─────────────────────────────────────────────────────

    /**
     * Serializes any object to a JSON string.
     * Supports: String, Number, Boolean, null, Instant, BigDecimal,
     *           List<?>, Map<String,?>, and any POJO (via getters).
     */
    public static String toJson(Object obj) {
        if (obj == null)               return "null";
        if (obj instanceof String s)   return quote(s);
        if (obj instanceof Boolean b)  return b.toString();
        if (obj instanceof Number n)   return n.toString();
        if (obj instanceof Instant i)  return quote(i.toString());
        if (obj instanceof BigDecimal bd) return bd.toPlainString();
        if (obj instanceof List<?> list) return serializeList(list);
        if (obj instanceof Map<?,?> map) return serializeMap(map);

        // Fallback: treat as POJO and read its getters
        return serializePojo(obj);
    }

    private static String serializeList(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(toJson(list.get(i)));
        }
        return sb.append(']').toString();
    }

    private static String serializeMap(Map<?,?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(quote(entry.getKey().toString()))
              .append(':')
              .append(toJson(entry.getValue()));
        }
        return sb.append('}').toString();
    }

    /**
     * Serializes a POJO by invoking all public getXxx() / isXxx() methods
     * (excluding getClass()) and mapping them to camelCase JSON field names.
     */
    private static String serializePojo(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Method method : obj.getClass().getMethods()) {
            String mName = method.getName();
            String fieldName = null;

            if (mName.startsWith("get") && mName.length() > 3
                    && !mName.equals("getClass")
                    && method.getParameterCount() == 0) {
                fieldName = Character.toLowerCase(mName.charAt(3)) + mName.substring(4);
            } else if (mName.startsWith("is") && mName.length() > 2
                    && method.getParameterCount() == 0
                    && (method.getReturnType() == boolean.class
                        || method.getReturnType() == Boolean.class)) {
                fieldName = Character.toLowerCase(mName.charAt(2)) + mName.substring(3);
            }

            if (fieldName == null) continue;

            try {
                Object value = method.invoke(obj);
                if (!first) sb.append(',');
                first = false;
                sb.append(quote(fieldName)).append(':').append(toJson(value));
            } catch (Exception e) {
                // skip unreadable field
            }
        }
        return sb.append('}').toString();
    }

    private static String quote(String s) {
        return '"' + s.replace("\\", "\\\\")
                      .replace("\"", "\\\"")
                      .replace("\n", "\\n")
                      .replace("\r", "\\r")
                      .replace("\t", "\\t") + '"';
    }

    // ─────────────────────────────────────────────────────
    // DESERIALIZATION  (JSON string → flat key-value map)
    // ─────────────────────────────────────────────────────

    /**
     * Parses a flat JSON object into a Map<String, String>.
     * Handles string, number, boolean, and null values.
     * Does NOT handle nested objects or arrays (not needed for our simple DTOs).
     *
     * Example: {"itemId":"biryani","quantity":2}
     * Result:  {itemId=biryani, quantity=2}
     */
    public static Map<String, String> parseFlat(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null || json.isBlank()) return map;

        // Match "key":"value" or "key":value pairs
        Pattern pair = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"([^\"]*)\"|([^,}\\s]+))");
        Matcher m = pair.matcher(json);
        while (m.find()) {
            String key   = m.group(1);
            String value = m.group(3) != null ? m.group(3) : m.group(4);
            if ("null".equals(value)) value = null;
            map.put(key, value);
        }
        return map;
    }

    /**
     * Parses a JSON array of strings: ["onion","nuts"] → List<String>
     */
    public static List<String> parseStringArray(String json) {
        List<String> result = new ArrayList<>();
        if (json == null || json.isBlank()) return result;
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        while (m.find()) result.add(m.group(1));
        return result;
    }

    /**
     * Extracts the raw value (array or primitive) for a given key from a JSON string.
     * Used to pull the excludeIngredients array from parsed intent JSON.
     */
    public static String extractRawValue(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\[[^\\]]*\\]|\"[^\"]*\"|[^,}\\s]+)");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
