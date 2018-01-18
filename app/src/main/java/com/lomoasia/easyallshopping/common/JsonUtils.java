package com.lomoasia.easyallshopping.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    private static Gson newGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static <T> T objectFromJson(String json, Class<T> classOfT) {
        try {
            return newGson().fromJson(json, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T objectFromJson(Reader json, Class<T> classOfT) {
        try {
            return newGson().fromJson(json, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T objectFromJson(String json, Type typeOfT) {
        try {
            return newGson().fromJson(json, typeOfT);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T objectFromJson(Reader json, Type typeOfT) {
        try {
            return newGson().fromJson(json, typeOfT);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> stringListFromJson(String json) {
        return objectFromJson(json, new TypeToken<List<String>>() {}.getType());
    }

    public static String objectToJson(Object src) {
        return newGson().toJson(src);
    }
}
