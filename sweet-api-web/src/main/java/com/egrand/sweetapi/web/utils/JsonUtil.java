package com.egrand.sweetapi.web.utils;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetapi.core.utils.MyJacksonModule;
import com.egrand.sweetapi.web.exception.OpenAlertException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JsonUtil {
    public JsonUtil() {
    }

    public static <T> String toJson(T value) {
        try {
            return getInstance().writeValueAsString(value);
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static byte[] toJsonAsBytes(Object object) {
        try {
            return getInstance().writeValueAsBytes(object);
        } catch (JsonProcessingException var2) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var2.getMessage());
        }
    }

    public static <T> T parse(String content, Class<T> valueType) {
        try {
            return getInstance().readValue(content, valueType);
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T parse(String content, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(content, typeReference);
        } catch (IOException var3) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var3.getMessage());
        }
    }

    public static <T> T parse(byte[] bytes, Class<T> valueType) {
        try {
            return getInstance().readValue(bytes, valueType);
        } catch (IOException var3) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var3.getMessage());
        }
    }

    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(bytes, typeReference);
        } catch (IOException var3) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var3.getMessage());
        }
    }

    public static <T> T parse(InputStream in, Class<T> valueType) {
        try {
            return getInstance().readValue(in, valueType);
        } catch (IOException var3) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var3.getMessage());
        }
    }

    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(in, typeReference);
        } catch (IOException var3) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var3.getMessage());
        }
    }

    public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
        try {
            if (!StrUtil.startWith(content, "[")) {
                content = "[" + content + "]";
            }

            List<Map<String, Object>> list = (List)getInstance().readValue(content, new TypeReference<List<Map<String, Object>>>() {
            });
            return (List)list.stream().map((map) -> {
                return toPojo(map, valueTypeRef);
            }).collect(Collectors.toList());
        } catch (IOException var3) {
            log.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static Map<String, Object> toMap(String content) {
        try {
            return (Map)getInstance().readValue(content, Map.class);
        } catch (IOException var2) {
            log.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
        try {
            Map<String, Map<String, Object>> map = (Map)getInstance().readValue(content, new TypeReference<Map<String, Map<String, Object>>>() {
            });
            Map<String, T> result = new HashMap(map.size());
            map.forEach((key, value) -> {
                result.put(key, toPojo(value, valueTypeRef));
            });
            return result;
        } catch (IOException var4) {
            log.error(var4.getMessage(), var4);
            return null;
        }
    }

    public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
        return getInstance().convertValue(fromValue, toValueType);
    }

    public static <T> T toPojo(JsonNode resultNode, Class<T> toValueType) {
        return getInstance().convertValue(resultNode, toValueType);
    }

    public static JsonNode readTree(String jsonString) {
        try {
            return getInstance().readTree(jsonString);
        } catch (IOException var2) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var2.getMessage());
        }
    }

    public static JsonNode readTree(InputStream in) {
        try {
            return getInstance().readTree(in);
        } catch (IOException var2) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var2.getMessage());
        }
    }

    public static JsonNode readTree(byte[] content) {
        try {
            return getInstance().readTree(content);
        } catch (IOException var2) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var2.getMessage());
        }
    }

    public static JsonNode readTree(JsonParser jsonParser) {
        try {
            return (JsonNode)getInstance().readTree(jsonParser);
        } catch (IOException var2) {
            throw new OpenAlertException(ErrorCode.JSON_PARSE_ERROR.getCode(), var2.getMessage());
        }
    }

    public static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    public static class JacksonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 1L;

        public JacksonObjectMapper() {
            super.setLocale(Locale.CHINA).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault())).setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)).configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true).configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true).findAndRegisterModules().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).getDeserializationConfig().withoutFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES});
            super.registerModule(new MyJacksonModule());
            super.findAndRegisterModules();
        }

        public ObjectMapper copy() {
            return super.copy();
        }
    }

    private static class JacksonHolder {
        private static final ObjectMapper INSTANCE = new JacksonObjectMapper();

        private JacksonHolder() {
        }
    }
}
