package com.example.redisdistributedlock.common.config

import com.example.redisdistributedlock.common.properties.RedisProperties
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    val redisProperties: RedisProperties
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().address = "redis://" + redisProperties.host + ":" + redisProperties.port
        return Redisson.create(config)
    }
}
