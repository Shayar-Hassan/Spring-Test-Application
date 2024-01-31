package com.ite5year.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class JsonHandler {


    public static String convertObjectToJsonString(Object object, String key) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(key, jsonElement);
        return jsonObject.toString();

    }

    public static <T> T convertJsonToObject(String jsonString, Class<T> tClass) {
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(jsonString, JsonObject.class);
        return gson.fromJson(object, tClass);
    }

    public static <T> T readJsonFromFileAndConvertItToObject(String fileName, Class<T> tClass) throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        return gson.fromJson(bufferedReader, tClass);
    }

    public static <T> T convertJsonToObject(String jsonString, Class<T> tClass, String key) {
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(jsonString, JsonObject.class);
        return gson.fromJson(object.get(key), tClass);
    }



}
