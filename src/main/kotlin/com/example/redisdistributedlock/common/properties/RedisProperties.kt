package com.example.redisdistributedlock.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("spring.redis")
class RedisProperties(
    var host: String,
    var port: Int,
) {

}
