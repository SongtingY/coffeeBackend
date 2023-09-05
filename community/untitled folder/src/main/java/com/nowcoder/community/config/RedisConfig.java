package com.nowcoder.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {
//    @Value("${spring.redis.database}")
//    private int redisDbIndex;
//
//    @Value("${spring.redis.host}")
//    private String hostName;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Bean
//    public RedisConnectionFactory connectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostName, port);
//        configuration.setDatabase(redisDbIndex);
//        return new JedisConnectionFactory(configuration);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        // 设置key序列化方式string
        redisTemplate.setKeySerializer(RedisSerializer.string()); // RedisSerializer.string() 等价于 new StringRedisSerializer()

        // 设置value的序列化方式json，使用GenericJackson2JsonRedisSerializer替换默认序列化
        redisTemplate.setValueSerializer(RedisSerializer.json()); // RedisSerializer.json() 等价于 new GenericJackson2JsonRedisSerializer()

        // 设置hash的key的序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        // 设置hash的value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        // 使配置生效
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
