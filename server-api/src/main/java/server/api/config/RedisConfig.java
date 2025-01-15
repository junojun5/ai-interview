package server.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    private final RedisProperties redisProperties;

    // Redis 연결을 위한 'Connection' 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    /**
     * Redis 데이터 처리를 위한 템플릿을 구성
     * 해당 구성된 RedisTemplate을 통해서 데이터 통신으로 처리되는 데이터에 대한 직렬화를 수행
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Redis를 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // Key-Value 형태로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 기본적으로 String형태의 직렬화를 수행
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    // Redis 설정을 spirng에서 변경하지 않도록 하는 설정
    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
