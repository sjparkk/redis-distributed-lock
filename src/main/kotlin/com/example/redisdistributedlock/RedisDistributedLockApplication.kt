package com.example.redisdistributedlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class RedisDistributedLockApplication

fun main(args: Array<String>) {
    runApplication<RedisDistributedLockApplication>(*args)
}
