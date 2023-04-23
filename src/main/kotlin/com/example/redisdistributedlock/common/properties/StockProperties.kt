package com.example.redisdistributedlock.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("redis.stock")
class StockProperties(
    var prefix: String,
) {
}
