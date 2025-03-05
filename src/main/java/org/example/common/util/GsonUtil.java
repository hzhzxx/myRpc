package org.example.common.util;

import com.google.gson.Gson;

public class GsonUtil {
    private final static Gson gson=new Gson();
    private GsonUtil() {

    }
    public static Gson getGson(){
        return gson;
    }
    public static <T> T fromObject(Object object,Class<T> tClass){
        String str=gson.toJson(object);
        return gson.fromJson(str,tClass);
    }
}
