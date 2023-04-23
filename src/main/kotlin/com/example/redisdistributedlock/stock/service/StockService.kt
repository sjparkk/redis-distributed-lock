package com.example.redisdistributedlock.stock.service

import com.example.redisdistributedlock.common.properties.StockProperties
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


@Service
class StockService(
    val redissonClient: RedissonClient,
    val stockProperties: StockProperties
) {

    companion object {
        private const val EMPTY_NUMBER = 0
    }

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Lock 을 이용한 재고 감소
     */
    fun decrease(key: String, count: Int) {
        val lockName = "$key:lock"
        val lock = redissonClient.getLock(lockName)
        val worker = Thread.currentThread().name

        try {
            if (!lock.tryLock(1, 3, TimeUnit.SECONDS)) return
            val stock = currentStock(key)
            if (stock <= EMPTY_NUMBER) {
                log.info("[$worker] 현재 남은 재고가 없습니다. (${stock}개)")
                return
            }
            log.info("현재 진행중 Worker : $worker & 현재 남은 재고 : ${stock}개")
            setStock(key, stock - count)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            if (lock != null && lock.isLocked) {
                lock.unlock()
            }
        }
    }

    fun decreaseNoLock(key: String?, count: Int) {
        val worker = Thread.currentThread().name
        val stock = currentStock(key)

        if (stock <= EMPTY_NUMBER) {
            log.info("[$worker] 현재 남은 재고가 없습니다. (${stock}개)")
            return
        }
        log.info("현재 진행중 Worker : $worker & 현재 남은 재고 : ${stock}개")
        setStock(key, stock - count)
    }

    fun keyGenerator(domain: String, keyId: String?): String {
        val prefix = "${stockProperties.prefix} : $domain: %s"
        return String.format(prefix, keyId)
    }

    fun setStock(key: String?, amount: Int) {
        redissonClient.getBucket<Int>(key).set(amount)
    }

    fun currentStock(key: String?): Int {
        val result = redissonClient.getBucket<Int>(key).get()
        log.info("현재 재고 수량 $result")
        return result
    }

}
