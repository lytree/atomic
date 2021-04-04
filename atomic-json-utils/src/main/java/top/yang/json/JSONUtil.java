package top.yang.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONUtil {
    private static ObjectMapper mapper = null;

    static {
        //noinspection ConstantConditions
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
    }

    public static String toJSONString(Object o) {
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> T parseObject(String jsonString, Class<T> elementClasses) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(jsonString, elementClasses);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> List<T> parseArray(String jsonString, Class<T> elementClasses) {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
        try {
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            return null;
        }
    }

}
