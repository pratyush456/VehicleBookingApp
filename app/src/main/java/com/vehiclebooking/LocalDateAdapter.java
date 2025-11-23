package com.vehiclebooking;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * Gson adapter for LocalDate serialization/deserialization
 * Uses ISO format (yyyy-MM-dd) for efficient parsing
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(FORMATTER));
    }
    
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            // Try ISO format first (efficient)
            return LocalDate.parse(json.getAsString(), FORMATTER);
        } catch (Exception e) {
            // Fallback to custom format if needed
            try {
                DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(json.getAsString(), customFormatter);
            } catch (Exception e2) {
                throw new JsonParseException("Unable to parse date: " + json.getAsString(), e2);
            }
        }
    }
}

