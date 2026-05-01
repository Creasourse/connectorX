package com.cs.service.impl;

import com.cs.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisCacheServiceImpl
 * @Author: wwd
 * @TODO:
 * @Date: 2025/8/14 10:44
 */
@Service
@Slf4j
public class RedisCacheServiceImpl<T> implements RedisCacheService<T> {

    @Resource(name = "bizRedisTemplate")
    private RedisTemplate<String, T> redisTemplate;

    @Resource(name = "bizRedisTemplateBytes")
    private RedisTemplate<String, byte[]> redisTemplateBytes;

    @Override
    public T get(String key) {
        try {
            if (key == null) {
                return null;
            } else {
                return redisTemplate.opsForValue().get(key);
            }
        } catch (Throwable t) {
            log.error("get key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public byte[] getBytes(String key) {
        try {
            if (key == null) {
                return null;
            } else {
                return redisTemplateBytes.opsForValue().get(key);
            }
        } catch (Throwable t) {
            log.error("get key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public T set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return value;
        } catch (Throwable t) {
            log.error("set key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public Long increment(String key, long value, long timeout) {
        try {
            Long result = redisTemplate.opsForValue().increment(key,value);
            redisTemplate.expire(key,timeout, TimeUnit.MILLISECONDS);

            return  result;
        } catch (Throwable t) {
            log.error("set key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public T set(String key, T value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return value;
        } catch (Throwable t) {
            log.error("set key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public T set(String key, T value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            return value;
        } catch (Throwable t) {
            log.error("set key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Throwable t) {
            log.error("delete key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void setBytes(String key, byte[] value) {
        try {
            redisTemplateBytes.opsForValue().set(key, value);
        } catch (Throwable t) {
            log.error("setBytes key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void setBytes(String key, byte[] value, long timeout) {
        try {
            redisTemplateBytes.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Throwable t) {
            log.error("setBytes key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void setBytes(String key, byte[] value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplateBytes.opsForValue().set(key, value, timeout, timeUnit);
        } catch (Throwable t) {
            log.error("setBytes key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }


    @Override
    public Long hincr(String key, String field ,Integer value) {
        try {
            return redisTemplate.opsForHash().increment(key,field,value);
        } catch (Throwable t) {
            log.error("hincr key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }


    @Override
    public Map<Object,Object> hgetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Throwable t) {
            log.error("hgetAll key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public boolean hasKey(String key,String field){
        try {
            return redisTemplate.opsForHash().hasKey(key,field);
        } catch (Throwable t) {
            log.error("hgetAll key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }


    @Override
    public void hset(String key,String field,Long value) {
        try {
            redisTemplate.opsForHash().put(key,field,value);
        } catch (Throwable t) {
            log.error("hset key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }


    @Override
    public void hdel(String key) {
        try {
            redisTemplate.opsForHash().delete(key);
        } catch (Throwable t) {
            log.error("hdel key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public Object hgetFiled(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Throwable t) {
            log.error("hgetFiled key [{}] exception!", key, t);
            throw new RuntimeException(t);
        }
    }
}
