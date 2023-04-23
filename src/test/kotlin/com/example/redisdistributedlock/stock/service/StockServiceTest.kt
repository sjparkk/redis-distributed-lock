package com.example.redisdistributedlock.stock.service

import com.example.redisdistributedlock.stock.domain.Stock
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.util.concurrent.CountDownLatch


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StockServiceTest(
    private var stockService: StockService
) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

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

    @Test
    @Order(1)
    @DisplayName("현재 재고 수량 확인")
    fun currentStock() {
        val amount: Int? = stock.amount
        val currentCount = stockService.currentStock(stockKey)
        assertEquals(amount, currentCount)
    }

    @Test
    @Order(2)
    @DisplayName("상품 재고 카운트만큼 감소")
    fun decreaseStockByCount() {
        val amount: Int? = stock.amount
        val count = 2
        stockService.decrease(stockKey, count)
        val currentCount = stockService.currentStock(stockKey)
        if (amount != null) {
            assertEquals(amount - count, currentCount)
        }
    }

    @Test
    @Order(3)
    @DisplayName("락 있는 경우 재고 감소 테스트")
    @Throws(InterruptedException::class)
    fun decreaseStockByLock() {

        log.info(":: 락 있는 경우 재고 감소 테스트")

        val people = 100
        val count = 2
        val soldOut = 0

        val workers = mutableListOf<Thread>()
        val countDownLatch = CountDownLatch(people)

        for(i in 1..people) {
            val thread = Thread(Worker(stockKey, count, countDownLatch))
            log.info("$i 번 쓰레드 생성 - ${thread.name}")
            workers.add(thread)
        }

        workers.forEach {
            log.info("$it 쓰레드 start() ")
            it.start()
        }

        countDownLatch.await()

        val currentCount = stockService.currentStock(stockKey)
        assertEquals(soldOut, currentCount)
    }

    @Test
    @Order(4)
    @DisplayName("락 없는 경우 재고 감소 테스트")
    @Throws(InterruptedException::class)
    fun decreaseStockByNoLock() {
        log.info(":: 락 없는 경우 재고 감소 테스트")

        val people = 100
        val count = 2
        val soldOut = 0

        val workers = mutableListOf<Thread>()
        val countDownLatch = CountDownLatch(people)

        for(i in 1..people) {
            val thread = Thread(NoLockWorker(stockKey, count, countDownLatch))
            log.info("$i 번 쓰레드 생성 - ${thread.name}")
            workers.add(thread)
        }

        workers.forEach {
            log.info("$it 쓰레드 start() ")
            it.start()
        }

        countDownLatch.await()
        val currentCount = stockService.currentStock(stockKey)
        assertNotEquals(soldOut, currentCount)
    }

    private inner class Worker(
        private val stockKey: String,
        private val count: Int,
        private val countDownLatch: CountDownLatch
    ): Runnable {
        override fun run() {
            stockService.decrease(this.stockKey, count)
            countDownLatch.countDown()
        }
    }

    private inner class NoLockWorker(
        private val stockKey: String,
        private val count: Int,
        private val countDownLatch: CountDownLatch
    ): Runnable {
        override fun run() {
            stockService.decreaseNoLock(this.stockKey, count)
            countDownLatch.countDown()
        }
    }

}

