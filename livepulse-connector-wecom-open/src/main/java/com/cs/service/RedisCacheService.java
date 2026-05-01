package com.cs.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisCacheService<T> {

    /**
     * 获取redis值
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 获取字节值
     * @param key
     * @return
     */
    byte[] getBytes(String key);

    /**
     * 设置redis值
     * @param key
     * @param value
     * @return
     */
    T set(String key, T value);

    /**
     * 设置自增redis值
     * @param key
     * @param value
     * @return
     */
    Long increment(String key, long value, long timeout);

    /**
     * 设置过期时间 默认时间单位/秒
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    T set(String key, T value, long timeout);

    /**
     * 设置过期时间
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     */
    T set(String key, T value, long timeout, TimeUnit timeUnit);

    /**
     * 删除redis
     *
     * @param key
     */
    void delete(String key);

    /**
     * 设置字节值
     *
     * @param key
     * @param value
     */
    void setBytes(String key, byte[] value);

    /**
     * 设置字节值过期时间默认秒
     *
     * @param key
     * @param value
     * @param timeout
     */
    void setBytes(String key, byte[] value, long timeout);

    /**
     * 设置字节值过期时间
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     */
    void setBytes(String key, byte[] value, long timeout, TimeUnit timeUnit);

    /**
     * @param key
     * @param field
     * @param value
     * @return
     */
    Long hincr(String key, String field, Integer value);


    /**
     * @param key
     * @return
     */
    Map<Object, Object> hgetAll(String key);

    /**
     * @param key
     * @param field
     * @return
     */
    boolean hasKey(String key,String field);

    /**
     * @param key
     * @param field
     * @param value
     */
    void hset(String key, String field, Long value);

    /**
     *
     * @param key
     */
    void hdel(String key);

    /**
     * @param key
     * @return
     */
    Object hgetFiled(String key, String field);

}
