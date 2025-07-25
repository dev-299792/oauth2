package com.example.clientapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class ObjectUtil {

    public static MultiValueMap<String, String> toMultiValueMap(Object pojo) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map= mapper.convertValue(pojo, Map.class);
        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();

        for(Map.Entry<String,Object> entry : map.entrySet()) {
            if(entry.getValue()!=null) {
                multiValueMap.add(entry.getKey(), entry.getValue().toString());
            }
        }
        return multiValueMap;
    }

}
