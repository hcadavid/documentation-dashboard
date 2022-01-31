/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import rug.icdtools.icddashboard.models.PipelineFailureDescription;

@Configuration
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        // Redis URL format: redis://[:password@]host[:port][/db-number][?option=value]
        try {
            
            String redisUrl = "redis://:redis123@localhost:6379/1";
            //redis://:pc87c38e8eaefd82a9f145c0922683960f96533675c8050f39004e209e4fa3f82@ec2-54-73-118-203.eu-west-1.compute.amazonaws.com:11969
            if (redisUrl==null){
                throw new RuntimeException("REDIS_URL sys env not defined.");
            }
            URI redistogoUri = new URI(redisUrl);
            JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
            jedisConnFactory.setUsePool(true);
            jedisConnFactory.setHostName(redistogoUri.getHost());
            jedisConnFactory.setPort(redistogoUri.getPort());
            jedisConnFactory.setPassword(redistogoUri.getUserInfo().split(":", 2)[1]);
            return jedisConnFactory;
        } catch (URISyntaxException ex) {
            Logger.getLogger(RedisConfig.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Malformed REDIS URI. Connection failed:"+ex.getLocalizedMessage(),ex);
        }

    }

    @Bean
    public RedisTemplate<String, PipelineFailureDescription> redisTemplate() {
        RedisTemplate<String, PipelineFailureDescription> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(PipelineFailureDescription.class));
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
    
    static class XXSerializer implements RedisSerializer<Object>{

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        
    }
}
