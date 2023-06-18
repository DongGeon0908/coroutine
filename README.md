# Coroutine

### 코루틴 책 읽고 공부
- [Concurrency란..](docs/concurrency_1.md)
- [Coroutine Dispatcher](docs/concurrency_2.md)
- [라이프 사이클과 에러 핸들링](docs/concurrency_3.md)
- [일시 중단 함수와 코루틴 컨텍스트](docs/concurrency_4.md)
- [이터레이터, 시퀀스 그리고 프로듀서](docs/concurrency_5.md)
- [채널-통신을 통한 메모리 공유](docs/concurrency_6.md)
- [스레드 한정, 액터 그리고 뮤텍스](docs/concurrency_7.md)
- [동시성 코드이 테스트와 디버깅](docs/concurrency_8.md)


### Coroutine
> 컴퓨터 프로그램 구성 요소 중 하나로 비선점형 멀티태스킹(non-preemptive multasking)을 수행하는 일반화한 서브루틴(subroutine)이다.
> 코루틴은 실행을 일시 중단(suspend)하고 재개(resume)할 수 있는 여러 진입 지점(entrypoint)을 허용한다.

### Dispatcher

- Thread에 코루틴을 보내는 역할을 수행
- 스레드 풀을 생성하고, Dispatcher를 통해 코루틴을 스레드에 분배한다.
- 코루틴을 사용하기 위해서는 스레드풀이 있어야 하고, 디스패처는 설정된 스레드풀에 코루틴을 배분하는 역할을 수행
- 코루틴이 스레드에 직접 접근은 불가, 디스패처를 통해서 접근

**Dispatcher의 종류**

- Dispatcher.Main : 메인스레드 디스패처
- Dispatcher.IO : file or network io 작업에 최적화된 디스패처
- Dispatcher.Default : cpu io 작업에 최적화된 디스패처
- Dispatcher.Unconfined : 코루틴이 호출된 스레드에서 진행되는 디스패처

```
The CoroutineDispatcher that is designed for offloading blocking IO tasks to a shared pool of threads.

Additional threads in this pool are created and are shutdown on demand. The number of threads used by tasks in this dispatcher is limited by the value of "kotlinx.coroutines.io.parallelism" (IO_PARALLELISM_PROPERTY_NAME) system property. It defaults to the limit of 64 threads or the number of cores (whichever is larger).
```

### 서브루틴
> 여러 명령어를 모아 이름을 부여해서 반복 호출할 수 있게 정의한 프로그램 구성 요소 == 함수

### 비선점형
> 멀티태스킹의 각 작업을 수행하는 참여자들의 실행을 운영체제가 강제로 일시 중단시키고 다른 참여자를 실행하게 만들 수 없음

### 코틀린의 코루틴 사용 과정
> 코틀린은 코루틴 빌더에 원하는 동작을 람다로 넘겨서 코루틴을 만들어 실행하는 방식으로 코루는을 활용

### 간단 코루틴

코틀린 기반의 코루틴(Coroutine)은 비동기 처리를 위한 라이브러리로, 동시성 프로그래밍을 보다 쉽고 직관적으로 구현할 수 있도록 돕는 기술입니다. 코루틴은 마치 함수 호출처럼 코드의 흐름을 제어하며, 일종의 경량 스레드(Lightweight Thread)로 작동합니다.

코루틴은 일반적인 함수와는 달리 실행 중 중단하고 재개될 수 있으며, 이를 통해 비동기 처리를 보다 쉽게 구현할 수 있습니다. 코루틴은 보통 suspend 함수를 사용하며, 이 함수가 호출되면 해당 코루틴이 일시 중단됩니다. 이후 다른 코루틴이 실행될 때까지 대기하다가, 다시 재개되어 실행됩니다.

코루틴은 크게 두 가지 개념인 'launch'와 'async-await'로 나뉩니다. launch는 일반적인 함수 호출과 유사한 방식으로 코루틴을 실행합니다. async-await는 비동기 처리 결과를 반환하는 함수를 호출할 때 사용되며, 결과를 기다리는 동안 다른 코루틴이 실행될 수 있도록 합니다.

코루틴을 사용하기 위해서는 kotlinx.coroutines 라이브러리가 필요합니다. 이 라이브러리는 코틀린 표준 라이브러리의 일부로 포함되어 있으며, Gradle이나 Maven과 같은 의존성 관리 도구를 사용하여 추가할 수 있습니다.

다음은 코루틴을 사용한 간단한 예제입니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        delay(1000L)
        println("World!")
    }
    print("Hello, ")
    job.join()
}
```
이 예제는 "Hello, World!"를 출력합니다. launch 함수를 사용하여 새로운 코루틴을 생성하고, delay 함수를 사용하여 1초간 대기한 후 "World!"를 출력합니다. 이후에는 "Hello, "를 출력한 후, job.join()을 호출하여 코루틴이 실행을 완료할 때까지 기다립니다.

다음은 async-await를 사용한 예제입니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferred = async {
        delay(1000L)
        "World!"
    }
    print("Hello, ")
    val result = deferred.await()
    println(result)
}
```
이 예제는 "Hello, World!"를 출력합니다. async 함수를 사용하여 결과를 반환하는 코루틴을 생성하고, delay 함수를 사용하여 1초간 대기한 후 "World!"를 반환

코루틴에서는 다양한 함수와 스코프가 제공되는데, 이를 적절하게 사용하면 보다 효율적인 비동기 처리를 할 수 있습니다.

### coroutineScope
coroutineScope는 일시 중단 가능한 함수 내에서 다른 코루틴을 실행하는 데 사용되는 스코프입니다. 일반적으로 runBlocking 함수 대신 사용됩니다. coroutineScope 내에서 실행된 코루틴이 완료될 때까지 해당 스코프에서 일시 중단됩니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope {
        launch {
            delay(500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope") 
    }

    println("Coroutine scope is over") 
}
```
이 예제에서는 runBlocking과 coroutineScope를 사용하여 각각 두 개의 코루틴을 실행합니다. 일반적으로 runBlocking은 실행된 코루틴이 완료될 때까지 대기하는 반면, coroutineScope는 실행된 코루틴이 완료될 때까지 해당 스코프에서 일시 중단됩니다.

결과적으로 위 코드는 다음과 같이 출력됩니다.

```
sql
Task from runBlocking
Task from coroutine scope
Task from nested launch
Coroutine scope is over
```

### withContext
withContext는 지정된 코루틴 디스패처에서 블록을 실행하도록 합니다. 디스패처는 코루틴이 실행되는 스레드 풀을 관리하며, 여러 개의 디스패처를 사용하여 다른 스레드에서 코루틴을 실행할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun calculate(value: Int): Int {
    delay(1000)
    return value + 10
}

fun main() = runBlocking {
    val time = measureTimeMillis {
        val result1 = withContext(Dispatchers.IO) { calculate(10) }
        val result2 = withContext(Dispatchers.IO) { calculate(20) }
        println("Result: ${result1 + result2}")
    }
    println("Time: $time ms")
}
```
이 예제에서는 Dispatchers.IO를 사용하여 백그라운드 스레드에서 두 개의 코루틴을 실행합니다. calculate 함수는 1초간 대기한 후, 인수로 전달된 값에 10을 더한 결과를 반환합니다.

결과적으로 위 코드는 다음과 같이 출력됩니다.

```yaml
Result: 40
Time: 1033 ms
```

### runBlocking
runBlocking은 코루틴이 모두 완료될 때까지 현재 스레드를 블록하는 함수입니다. 일반적으로 테스트 코드에서 사용

### Reference

- https://github.com/Kotlin/KEEP/blob/master/proposals/coroutines.md
