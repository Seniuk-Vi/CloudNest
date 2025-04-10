package org.brain.uploadservice.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@AllArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory connectionFactory() {
        // Create LettuceConnectionFactory using properties from application.yml
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                redisProperties.getHost(),
                redisProperties.getPort()
        );
        return connectionFactory;
    }


    @Bean
    RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        return template;
    }
}
