/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.config;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import rug.icdtools.icddashboard.models.PublishedICDMetadata;

/**
 *
 * @author hcadavid
 * @param <T>
 */
public class RedisTemplateBuilder<T> {

    private Class<T> typeClass;
    JedisConnectionFactory cfactory;
    
    public RedisTemplateBuilder(Class<T> typeClass, JedisConnectionFactory cf) {
        this.typeClass=typeClass;
        this.cfactory=cf;
    }
     
    
    public RedisTemplate<String,T> buildRedisTemplate(){
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(cfactory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer(typeClass));
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }
    
}
