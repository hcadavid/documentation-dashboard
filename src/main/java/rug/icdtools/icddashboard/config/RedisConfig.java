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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;
import rug.icdtools.icddashboard.models.PublishedICDMetadata;

@Configuration
@EnableTransactionManagement
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        // Redis URL format: redis://[:password@]host[:port][/db-number][?option=value]
        try {
            String redisUrl = System.getenv("REDIS_URL");
            
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
    public RedisTemplate<String, PipelineFailureDetails> pipelineFailuresRedisTemplate() {
        return new RedisTemplateBuilder<>(PipelineFailureDetails.class, jedisConnectionFactory()).buildRedisTemplate();
    }

        
    @Bean
    public RedisTemplate<String, PipelineFailure> piplineFailureRedisTemplate() {
        return new RedisTemplateBuilder<>(PipelineFailure.class, jedisConnectionFactory()).buildRedisTemplate();
    }
      

    @Bean
    public RedisTemplate<String, PublishedICDMetadata> metadataRedisTemplate() {
        return new RedisTemplateBuilder<>(PublishedICDMetadata.class, jedisConnectionFactory()).buildRedisTemplate();
    }
      

    
}
