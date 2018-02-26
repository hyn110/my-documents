package com.fmi110.mmall.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 静态内部类实现单例模式
 * json工具类,使用 jacksonJSON
 */
public final class JsonUtils {
    private JsonUtils() {}
    private static final Locale CHINA = Locale.CHINA; // 时区
    private static boolean config = false;

    /**
     * 配置ObjectMapper对象,设置时区 , 解决json转换后时间类型相差8小时的问题
     */
     static {
        if (!config) {
            ObjectMapper instance = getInstance();
            instance.setLocale(CHINA);
            // 返回值为时间戳(long)
//          instance.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            instance.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", CHINA));
            instance.setTimeZone(TimeZone.getTimeZone("GMT+8"));

//            instance.enable(JsonGenerator.Feature.IGNORE_UNKNOWN, JsonGenerator.Feature.QUOTE_FIELD_NAMES);
            // null值 不序列化
            instance.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // 转换空对象时不抛出异常,即不存在 set get 方法
            instance.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            // json中存在,但是java对象中不存在对应属性时不抛异常
            instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }
    /**
     * 将对象序列化成json字符串 , 不输出值为 NULL 的字段
     * @param object javaBean
     * @return jsonString json字符串
     */
    public static String toJson(Object object) {
//        config();
        try {
            return getInstance().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出格式化后的json串,不输出值为 NULL 的字段
     * @param object
     * @return
     */
    public static String toJsonPretty(Object object) {
//        config();
        try {
            return getInstance().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    /**
//     * 输出json字符串,是否输出 NULL 值
//     * @param object
//     * @param includeNULL  是否输出 null
//     * @return
//     */
//    public static String toJson(Object object,boolean includeNULL) {
//        try {
//            if (includeNULL) {
//                getInstance().setSerializationInclusion(JsonInclude.Include.ALWAYS);
//            }
//            return getInstance().writeValueAsString(object);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 将json反序列化成对象
     * @param jsonString jsonString
     * @param valueType class
     * @param <T> T 泛型标记
     * @return Bean
     */
    public static <T> T parse(String jsonString, Class<T> valueType) {
//        config();
        try {
            return getInstance().readValue(jsonString, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 带泛型参数的集合的反序列化
     * @param jsonString
     * @param valueTypeRef
     * <p>new TypeReference<List<OrderDTO>>(){}</p>
     * <p>new TypeReference<Hashmap<String,OrderDTO>>(){}</p>
     * @param <T>
     * @return
     */
    public static <T> T parse(String jsonString, TypeReference valueTypeRef){
//        config();
        try {
            return getInstance().readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 带泛型参数的集合的反序列化
     * @param jsonString
     * @param collectionClass  集合的字节码对象
     * @param elementClass  集合中元素的字节码对象
     * @param <T>
     * @return
     */
    public static <T> T parse(String jsonString, Class<?> collectionClass,Class<?>... elementClass){
        JavaType javatype = getInstance().getTypeFactory()
                                         .constructParametricType(collectionClass, elementClass);
        try {
            return getInstance().readValue(jsonString, javatype);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }


    private static class JacksonHolder {
        private static ObjectMapper INSTANCE = new ObjectMapper();
    }

//    public static void  main(String[] a){
//        ArrayList<User> list = new ArrayList<>();
//        list.add(new User());
//
//        String s = toJsonPretty(list);
//        System.out.println(s);
//        List<User> l = parse(s, List.class, User.class);
//        System.out.println(l);
//    }
}

//class User{
//    private String name;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//}