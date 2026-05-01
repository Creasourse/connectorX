package com.cs.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableConfigurationProperties({RedisProperties.class})
public class BizRedisConfig extends CachingConfigurerSupport {

    private final static Logger log = LoggerFactory.getLogger(BizRedisConfig.class);

    private RedisProperties bizRedisCustomProperties;

    public BizRedisConfig(RedisProperties bizRedisCustomProperties) {
        this.bizRedisCustomProperties = bizRedisCustomProperties;
    }

    @Autowired
    private ObjectMapper objectMapper;
    private LettuceConnectionFactory lettuceConnectionFactory = null;

    public RedisConnectionFactory getLettuceConnectionFactory(){
        return  bizRedisConnectionFactory(this.bizRedisCustomProperties);
    }

    /**
     * 自定义redis配置工厂
     * @param bizRedisCustomProperties
     * @return
     */

    private RedisConnectionFactory bizRedisConnectionFactory(RedisProperties bizRedisCustomProperties) {
        /**
         * 单机模式配置
         * livepulse.stream.cache.biz.redis.host=localhost
         * livepulse.stream.cache.biz.redis.port=6379
         * 哨兵模式配置
         * livepulse.stream.cache.biz.redis.sentinel.master=mymaster
         * livepulse.stream.cache.biz.redis.sentinel.nodes=10.25.19.1:26379,10.25.19.2:26379,10.25.19.3:26379
         * 集群模式配置
         * livepulse.stream.cache.biz.redis.cluster.nodes=10.25.19.1:7000,10.25.19.1:7001,10.25.19.2:7002,10.25.19.2:7003,10.25.19.3:7004,10.25.19.3:7005
         * livepulse.stream.cache.biz.redis.cluster.max-redirects=3
         */
        if(lettuceConnectionFactory != null){

            return  lettuceConnectionFactory;
        }
        // Lettuce客户端配置 连接池
        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig(bizRedisCustomProperties)).build();

        // 哨兵模式
        if (bizRedisCustomProperties.getSentinel() != null) {
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
            redisSentinelConfiguration.setDatabase(bizRedisCustomProperties.getDatabase());
            redisSentinelConfiguration.setMaster(bizRedisCustomProperties.getSentinel().getMaster());
            Set<RedisNode> sentinels = new HashSet<>();
            List<String> sentinelNodes = bizRedisCustomProperties.getSentinel().getNodes();
            for (String node : sentinelNodes) {
                String[] nodes = node.split(":");
                RedisNode redisNode = new RedisNode(nodes[0], Integer.parseInt(nodes[1]));
                sentinels.add(redisNode);
            }
            redisSentinelConfiguration.setSentinels(sentinels);
            redisSentinelConfiguration.setPassword(bizRedisCustomProperties.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration);
        }
        // 集群模式
        else if (bizRedisCustomProperties.getCluster() != null) {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            Set<RedisNode> clusterNodes = new HashSet<>();
            List<String> clusters = bizRedisCustomProperties.getCluster().getNodes();
            for (String node : clusters) {
                String[] nodes = node.split(":");
                RedisNode redisNode = new RedisNode(nodes[0], Integer.parseInt(nodes[1]));
                clusterNodes.add(redisNode);
            }
            redisClusterConfiguration.setClusterNodes(clusterNodes);
            redisClusterConfiguration.setMaxRedirects(bizRedisCustomProperties.getCluster().getMaxRedirects());
            redisClusterConfiguration.setPassword(bizRedisCustomProperties.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);

        }
        // 单机模式
        else {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setDatabase(bizRedisCustomProperties.getDatabase());
            redisStandaloneConfiguration.setHostName(bizRedisCustomProperties.getHost());
            redisStandaloneConfiguration.setPort(bizRedisCustomProperties.getPort());
            redisStandaloneConfiguration.setPassword(bizRedisCustomProperties.getPassword());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        }

        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    /**
     * 设置连接池
     * @param bizRedisCustomProperties
     * @return
     */
    private GenericObjectPoolConfig genericObjectPoolConfig(RedisProperties bizRedisCustomProperties) {
        RedisProperties.Pool pool = new RedisProperties.Pool();
        // 返回Lettuce默认的配置
        if (bizRedisCustomProperties.getLettuce().getPool() != null) {
            pool = bizRedisCustomProperties.getLettuce().getPool();
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 配置文件中的池配置参数 如有新增池配置需要在这里设置
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        return poolConfig;
    }

    /**
     * retemplate相关配置
     *
     * @return
     */
    @Bean(value = "bizRedisTemplate")
    public RedisTemplate<String, Object> executorRedisTemplate() {

        RedisConnectionFactory factory = bizRedisConnectionFactory(bizRedisCustomProperties);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * redis序列化值为字节
     * @return
     */
    @Bean(value = "bizRedisTemplateBytes")
    public RedisTemplate<String, byte[]> executorRedisTemplateBytes() {
        RedisConnectionFactory  factory = bizRedisConnectionFactory(bizRedisCustomProperties);


        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        // redis默认序列为数组
        template.setKeySerializer(new StringRedisSerializer());
        // value 采用byte数组序列化方式
        template.setValueSerializer(RedisSerializer.byteArray());
        template.afterPropertiesSet();

        return template;
    }



}
