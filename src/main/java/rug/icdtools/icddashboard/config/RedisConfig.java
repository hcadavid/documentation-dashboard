/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rug.icdtools.icddashboard.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import rug.icdtools.core.models.PublishedICDMetadata;
import rug.icdtools.core.models.VersionedDocument;
import rug.icdtools.icddashboard.models.PipelineFailure;
import rug.icdtools.icddashboard.models.PipelineFailureDetails;
import rug.icdtools.icddashboard.models.ICDStatusDescription;

@Configuration
@EnableTransactionManagement
public class RedisConfig extends CachingConfigurerSupport {

   
    @Bean
    public JedisClientConfiguration jedisClientConfiguration() {
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder JedisPoolingClientConfigurationBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(64);
        genericObjectPoolConfig.setMaxTotal(64);
        genericObjectPoolConfig.setMinIdle(5);
        return JedisPoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig).build();
    }

    
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        // Redis URL format: redis://[:password@]host[:port][/db-number][?option=value]
        try {
            String redisUrl = System.getenv("REDIS_URL");
            
            if (redisUrl==null){
                throw new RuntimeException("REDIS_URL sys env not defined.");
            }
            
            URI redistogoUri = new URI(redisUrl);

            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setDatabase(0);
            redisStandaloneConfiguration.setHostName(redistogoUri.getHost());
            redisStandaloneConfiguration.setPassword(redistogoUri.getUserInfo().split(":", 2)[1]);
            redisStandaloneConfiguration.setPort(redistogoUri.getPort());
            return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration());


            /*
            JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
            jedisConnFactory.setUsePool(true);
            jedisConnFactory.setHostName(redistogoUri.getHost());
            jedisConnFactory.setPort(redistogoUri.getPort());
            jedisConnFactory.setPassword(redistogoUri.getUserInfo().split(":", 2)[1]);
            return jedisConnFactory;*/
        } catch (URISyntaxException ex) {
            Logger.getLogger(RedisConfig.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Malformed REDIS URI. Connection failed:"+ex.getLocalizedMessage(),ex);
        }

    }

    
    /*@Bean
    RedisTemplate genericRedisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        template.setEnableTransactionSupport(true);
        return template;

    }*/

    private <T> RedisTemplate<String,T> genericRedisTemplate(Class<T> valueType){
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(valueType));
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer(valueType));
        template.setEnableTransactionSupport(true);
        return template;
    
    }

    @Bean
    RedisTemplate objectRedisTemplate() {
        return genericRedisTemplate(Object.class);
    }
    

    @Bean
    RedisTemplate<String, PublishedICDMetadata> publishedICDMetadataTemplate(){
        return genericRedisTemplate(PublishedICDMetadata.class);
    }

    @Bean
    RedisTemplate<String, PipelineFailureDetails> failurDetailsTemplate(){
        return genericRedisTemplate(PipelineFailureDetails.class);
    }

    @Bean
    RedisTemplate<String, PipelineFailure> failureTemplate(){
        return genericRedisTemplate(PipelineFailure.class);
    }


    @Bean
    RedisTemplate<String, VersionedDocument> versionedDocumentTemplate(){
        return genericRedisTemplate(VersionedDocument.class);
    }

    @Bean
    RedisTemplate<String, ICDStatusDescription> icdStatusDescriptionTemplate(){
        return genericRedisTemplate(ICDStatusDescription.class);
    }

    
}
