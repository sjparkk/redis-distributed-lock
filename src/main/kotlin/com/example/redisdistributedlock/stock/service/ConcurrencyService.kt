package com.example.redisdistributedlock.stock.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ConcurrencyService {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var age: Int? = null

    fun getAge(age: Int) {
        this.age = age
        log.info("저장 age={} -> age={}", age, this.age)
        sleep(1000)
        log.info("조회 age ={}", this.age)
    }

    private fun sleep(millis: Int) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}