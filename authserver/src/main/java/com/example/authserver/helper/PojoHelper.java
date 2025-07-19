package com.example.authserver.helper;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PojoHelper {

    public static Map<String, String> convertToMap(Object object) {
        Map<String, String> fieldMap = new LinkedHashMap<>();
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(object)!=null)
                    fieldMap.put(camelCaseToDisplayName(field.getName()), String.valueOf(field.get(object)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldMap;
    }

    public static String camelCaseToDisplayName(String camelCaseString) {
        if (camelCaseString == null || camelCaseString.isEmpty()) {
            return camelCaseString;
        }

        Pattern CAMEL_CASE_SPLIT_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");
        String splitString = CAMEL_CASE_SPLIT_PATTERN.matcher(camelCaseString).replaceAll(" ");
        return splitString.substring(0, 1).toUpperCase() + splitString.substring(1);
    }

}
