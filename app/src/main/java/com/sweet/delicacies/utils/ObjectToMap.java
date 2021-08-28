package com.sweet.delicacies.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectToMap {

    public  static Map<String, Object> MapObject(Object object){
        Map<String, Object> map = new HashMap<>();
        for(Field f: object.getClass().getDeclaredFields()){
            f.setAccessible(true);
            try {
                map.put(f.getName(), f.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
