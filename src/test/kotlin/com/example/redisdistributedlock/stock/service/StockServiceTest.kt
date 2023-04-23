package com.example.redisdistributedlock.stock.service

import com.example.redisdistributedlock.stock.domain.Stock
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream


@SpringBootTest
internal class StockServiceTest {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var stockService: StockService
    private lateinit var stockKey: String
    private lateinit var stock: Stock

    @BeforeEach
    @DisplayName("레디스 키와 재고 설정")
    fun stockSetup() {
        val name = "apple"
        val keyId = "001"
        val amount = 100
        val apple = Stock(name, keyId, amount)
        stockKey = stockService.keyGenerator(apple.name, apple.keyId)
        this.stock = apple
        stockService.setStock(stockKey, amount)
    }

}

