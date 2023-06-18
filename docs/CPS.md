# CPS 간단 설명

- CPS는 "Continuation Passing Style(계속 전달 스타일)"의 약어로, 프로그래밍 언어에서 제어 흐름을 처리하는 방법 중 하나
- CPS는 함수를 호출하고 그 함수의 결과를 처리하는 대신, 결과를 다음 단계로 전달하는 방식으로 동작
- 일반적으로 함수 호출은 결과를 반환하는 것으로 간주됩니다. 그러나 CPS에서는 함수가 결과를 직접 반환하는 대신, 추가적인 매개 변수인 "계속 전달자(continuation)"라는 콜백 함수를 받아들입니다. 함수는 결과를 계속 전달자에게 전달하고, 계속 전달자는 다음으로 실행될 작업을 정의하는 데 사용됩니다.
- CPS를 사용하면 비동기 작업이나 제어 흐름의 복잡한 구조를 처리하는 데 유용 예를 들어, 콜백 함수를 사용하여 비동기 작업의 완료를 처리할 수 있습니다. CPS는 또한 제어 흐름의 상태를 명시적으로 전달하므로 예외 처리나 에러 핸들링에 유리할 수 있습니다.
- CPS는 일부 함수형 프로그래밍 언어에서 많이 사용되며, 함수형 언어에서는 모나드나 프라미스와 함께 사용될 때 유용한 패턴이 될 수 있습니다.

### Example - 1

```kotlin
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

// 비동기 작업을 나타내는 컨텍스트
class AsyncTaskContext : Continuation<Unit> {
    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        println("비동기 작업 완료!")
    }
}

// 비동기 작업을 수행하는 함수
fun performAsyncTask(callback: (Unit) -> Unit) {
    // 비동기 작업 수행 후 결과를 전달하는 로직
    println("비동기 작업 시작")
    Thread.sleep(2000) // 시뮬레이션을 위해 2초 대기
    callback(Unit) // 결과를 전달
}

fun main() {
    val asyncTaskContext = AsyncTaskContext()

    // 코루틴 실행
    performAsyncTask.asyncCoroutine(asyncTaskContext)
    
    println("메인 스레드 실행 중")
    Thread.sleep(3000) // 시뮬레이션을 위해 3초 대기
    println("메인 스레드 종료")
}

// CPS를 사용한 코루틴 확장 함수
fun <T> ((T) -> Unit).asyncCoroutine(continuation: Continuation<Unit>) {
    startCoroutine(continuation)
}

```

### Example - 2

```kotlin
import kotlin.coroutines.*

// 비동기 작업을 나타내는 컨텍스트
class AsyncTaskContext : Continuation<Int> {
    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Int>) {
        println("비동기 작업 완료: ${result.getOrNull()}")
    }
}

// 비동기 작업을 수행하는 함수
fun performAsyncTask(a: Int, b: Int, callback: (Int) -> Unit) {
    // 비동기 작업 수행 후 결과를 전달하는 로직
    println("비동기 작업 시작: $a + $b")
    Thread.sleep(2000) // 시뮬레이션을 위해 2초 대기
    val result = a + b
    callback(result) // 결과를 전달
}

// 두 숫자를 비동기적으로 더하는 코루틴
suspend fun addAsync(a: Int, b: Int): Int = suspendCoroutine { continuation ->
    performAsyncTask(a, b) { result ->
        continuation.resume(result)
    }
}

fun main() {
    val asyncTaskContext = AsyncTaskContext()

    // 코루틴 실행
    GlobalScope.launch(asyncTaskContext) {
        val result = addAsync(10, 20)
        println("결과: $result")
    }

    println("메인 스레드 실행 중")
    Thread.sleep(3000) // 시뮬레이션을 위해 3초 대기
    println("메인 스레드 종료")
}

```
