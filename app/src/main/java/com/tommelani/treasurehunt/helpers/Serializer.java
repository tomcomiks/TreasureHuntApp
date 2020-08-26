package com.tommelani.treasurehunt.helpers;

import android.graphics.RectF;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Thomas on 03/06/2016.
 */
public class Serializer {

    static Type typeSetLong = new TypeToken<HashSet<Long>>() {
    }.getType();
    static Type typeSetInteger = new TypeToken<HashSet<Integer>>() {
    }.getType();
    static Type typeListString = new TypeToken<ArrayList<String>>() {
    }.getType();
    static Type typeListRectF = new TypeToken<ArrayList<RectF>>() {
    }.getType();


    /*
    * Serializer/Deserializer for Long type
    * */
    public static Set<Integer> deserializeToSetInteger(String json) {
        Gson gson = new Gson();
        Set<Integer> arrayList = gson.fromJson(json, typeSetInteger);
        if (arrayList == null) return new HashSet<>();
        return arrayList;
    }

    public static String serializeFromSetInteger(Set<Integer> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList, typeSetInteger);
        return json;
    }

    public static String addIntegerToSet(String json, int value) {
        Set<Integer> arrayList = deserializeToSetInteger(json);
        arrayList.add(value);
        String result = serializeFromSetInteger(arrayList);
        return result;
    }

    /*
    * Serializer/Deserializer for Long type
    * */
    public static Set<Long> deserializeToSetLong(String json) {
        Gson gson = new Gson();
        Set<Long> arrayList = gson.fromJson(json, typeSetLong);
        if (arrayList == null) return new HashSet<>();
        return arrayList;
    }

    public static String serializeFromSetLong(Set<Long> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList, typeSetLong);
        return json;
    }

    public static String addLongToList(String json, long value) {
        Set<Long> arrayList = deserializeToSetLong(json);
        arrayList.add(value);
        String result = serializeFromSetLong(arrayList);
        return result;
    }

    /*
    * Serializer/Deserializer for String type
    * */
    public static List<Object> deserializeToListString(String json) {
        Gson gson = new Gson();
        List<Object> arrayList = gson.fromJson(json, typeListString);
        if (arrayList == null) return new ArrayList<>();
        return arrayList;
    }

    public static String serializeFromListString(List<Object> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList, typeListString);
        return json;
    }

    public static String addStringToList(String json, String value) {
        List<Object> arrayList = deserializeToListString(json);
        arrayList.add(value);
        String result = serializeFromListString(arrayList);
        return result;
    }

    /*
    * Serializer/Deserializer for RectF type
    * */
    public static List<Object> deserializeToListRectF(String json) {
        Gson gson = new Gson();
        List<Object> arrayList = gson.fromJson(json, typeListRectF);
        if (arrayList == null) return new ArrayList<>();
        return arrayList;
    }

    public static String serializeFromListRectF(List<Object> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList, typeListRectF);
        return json;
    }

    public static String addRectFToList(String json, RectF value) {
        List<Object> arrayList = deserializeToListRectF(json);
        arrayList.add(value);
        String result = serializeFromListRectF(arrayList);
        return result;
    }


}
