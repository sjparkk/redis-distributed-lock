package com.example.redisdistributedlock.stock.service

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * 동시성 이슈?
 * Redisson에 대해서 알아보기 위해 먼저 동시성 이슈가 무엇인지에 대해서 알아본다.
 * 동시성 이슈란, 동일한 자원에 대해  둘 이상의 스레드가 동시에 제어할 때 나타나는 문제이다.
 */
@SpringBootTest
class ConcurrencyServiceTest {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var concurrencyService: ConcurrencyService

    @Test
    fun concurrencyTest() {
        log.info("start")
        val threadA = Thread { concurrencyService.getAge(30) }
        val threadB = Thread { concurrencyService.getAge(20) }
        threadA.start()
        sleep(100)
        threadB.start()
        sleep(3000)
        log.info("exit")
    }

    /**
    테스트 코드의 동작을 유추해 본다면 threadA, threadB 두개의 스레드가 생성되고 threadA start() 호출을 통해 쓰레드A가 실행되고 sleep(100)을 통해 잠시 후 쓰레드B가 실행되어 동시성을 고려하지 않았다면 다음과 같이 결과값이 출력될 것이라고 생각할 수 있다.
     예샹 결과
     [    Test worker] c.e.r.s.service.ConcurrencyServiceTest   : start
     [       Thread-4] c.e.r.stock.service.ConcurrencyService   : 저장 age=30 -> age=30
     [       Thread-4] c.e.r.stock.service.ConcurrencyService   : 조회 age =30
     [       Thread-5] c.e.r.stock.service.ConcurrencyService   : 저장 age=20 -> age=20
     [       Thread-5] c.e.r.stock.service.ConcurrencyService   : 조회 age =20
     [    Test worker] c.e.r.s.service.ConcurrencyServiceTest   : exit
     실제 결과
     [    Test worker] c.e.r.s.service.ConcurrencyServiceTest   : start
     [       Thread-4] c.e.r.stock.service.ConcurrencyService   : 저장 age=30 -> age=30
     [       Thread-5] c.e.r.stock.service.ConcurrencyService   : 저장 age=20 -> age=20
     [       Thread-4] c.e.r.stock.service.ConcurrencyService   : 조회 age =20
     [       Thread-5] c.e.r.stock.service.ConcurrencyService   : 조회 age =20
     [    Test worker] c.e.r.s.service.ConcurrencyServiceTest   : exit
     */

    /**
    이렇게 결과가 나오는 이슈는 쓰레드A가 getAge() 함수를 통해 age 값을 30으로 변경 후에 1초 대기 상태로 들어간 후 쓰레드B가 getAge()를 호출하여 age 값을 20으로 변경하게 된다.
    쓰레드A가 sleep 이후 age 값을 읽게되면 쓰레드B가 변경한 값을 읽어들인다.

    이러한 동시성 문제는 지역 변수에 대해서는 쓰레드마다 다른 메모리 영역이 할당 받기 때문에 발생하지 않지만 인스턴스 필드 또는 static과 같은 공용 필드에 접근에 대해서 발생한다.

    또 여기서 중요한 점은 동시성 문제란 동일한 자원에 대해서 접근한다고 무조건 발생하는 것이 아닌 동시에 접근한 자원에 대해서 변경이 일어나는 경우 발생하는 문제이다. 즉, 변경하지 않고 읽기만 한다면 발생하지 않는다는 것이다.

    해당 개념을 확장 시켜 데이터베이스에 저장되어 있는 자원에 대해서 접근할 때도 마찬가지이다.
     */

    private fun sleep(millis: Int) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}