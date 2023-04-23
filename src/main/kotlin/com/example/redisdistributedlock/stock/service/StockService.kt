package com.example.redisdistributedlock.stock.service

import com.example.redisdistributedlock.common.properties.StockProperties
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


@Service
class StockService(
    val stockProperties: StockProperties
) {

    companion object {
        private const val EMPTY_NUMBER = 0
    }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun keyGenerator(domain: String, keyId: String?): String {
        val prefix = "${stockProperties.prefix} : $domain: %s"
        return String.format(prefix, keyId)
    }
}