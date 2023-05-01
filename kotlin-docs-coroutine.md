# kotlin-docs-coroutine

Kotlin Coroutines는 비동기 프로그래밍을 위한 Kotlin 라이브러리입니다. 이 라이브러리는 비동기적으로 실행되는 코드를 작성하고 관리하는 데 사용됩니다. 코루틴을 사용하면 시간이 오래 걸리는 작업을 수행할 때 UI를 차단하지 않고도 앱의 반응성을 유지할 수 있습니다. 코루틴은 일반적으로 대기 시간이 많은 작업, I/O 작업 및 네트워크 작업을 수행하는 데 사용됩니다.

### 코루틴이란?
코루틴은 코루틴 빌더와 함께 실행되는 논블로킹 함수입니다. 코루틴이 실행되면 해당 함수는 일시 중지되고 다른 코드가 실행됩니다. 이후에 코루틴이 다시 시작될 때 일시 중지된 지점에서 실행이 계속됩니다.

코루틴을 시작하려면 launch 빌더를 사용합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
```
위 코드는 runBlocking 함수를 사용하여 main 함수를 코루틴으로 변환합니다. launch 빌더를 사용하여 코루틴을 시작하고 delay 함수를 사용하여 1초 동안 일시 중지합니다. 그런 다음 "World!"를 출력합니다. 마지막으로 "Hello,"를 출력합니다.

### 코루틴 취소
코루틴이 실행 중일 때 취소할 수 있습니다. 이를 위해 cancel 함수를 사용합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("Job: I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancel() // 취소
    job.join() // 취소를 기다립니다.
    println("main: Now I can quit.")
}
```
위 코드는 launch 빌더를 사용하여 코루틴을 시작합니다. 이 코루틴은 0.5초마다 반복되는 작업을 수행합니다. delay 함수를 사용하여 코루틴을 일시 중지합니다. 1.3초 후에 "main: I'm tired of waiting!"을 출력하고 cancel 함수를 사용하여 코루틴을 취소합니다. 마지막으로 join 함수를 사용하여 취소를 기다린 후 "main: Now I can quit."을 출력합니다.

### 코루틴 디스패처
코루틴 디스패처는 코루틴이 실행될 스레드를 지정합니다. 코루틴 디스패처를 사용하여 코루틴이 실행될 스레드를 지정하면 코루틴이 다른 스레드에서 실행될 수 있습니다. 이를 통해 I/O 작업과 같은 작업을 수행하는 동안 UI를 차단하지 않고도 앱의 반응성을 유지할 수 있습니다.

기본 디스패처는 Dispatchers.Default입니다. 이 디스패처는 백그라운드 스레드 풀을 사용하여 작업을 실행합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch { // 코루틴 1
        println("코루틴 1 실행 중 - ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) { // 코루틴 2
        println("코루틴 2 실행 중 - ${Thread.currentThread().name}")
    }
}
```
위 코드는 runBlocking 함수를 사용하여 main 함수를 코루틴으로 변환합니다. 두 개의 코루틴을 시작합니다. 첫 번째 코루틴은 기본 디스패처를 사용하여 실행됩니다. 두 번째 코루틴은 Dispatchers.Default 디스패처를 사용하여 실행됩니다. 코루틴이 실행될 때마다 Thread.currentThread().name을 사용하여 실행 중인 스레드의 이름을 출력합니다.

### 코루틴 스코프
코루틴 스코프는 코루틴이 실행될 범위를 정의합니다. 코루틴이 실행 중인 스코프에서 취소되면 해당 스코프에서 시작된 모든 코루틴이 취소됩니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = Job()
    val scope = CoroutineScope(job)
    scope.launch {
        delay(1000L)
        println("코루틴 실행 중")
    }
    delay(500L)
    job.cancel()
    delay(1000L)
    println("코루틴 실행 취소")
}
```
위 코드는 runBlocking 함수를 사용하여 main 함수를 코루틴으로 변환합니다. Job과 CoroutineScope를 사용하여 코루틴 스코프를 정의합니다. launch 함수를 사용하여 코루틴을 시작하고 delay 함수를 사용하여 1초 동안 일시 중지합니다. 0.5초 후에 job.cancel()을 호출하여 코루틴 실행을 취소합니다. 

### 코루틴 예외 처리
코루틴에서 예외가 발생하면 코루틴은 자동으로 취소됩니다. 예외 처리는 try-catch 블록으로 수행할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        try {
            delay(Long.MAX_VALUE)
        } catch (e: Exception) {
            println("예외 처리 - ${e.message}")
        } finally {
            println("취소됨")
        }
    }
    delay(1000L)
    job.cancel()
    job.join()
    println("main 끝")
}
```
위 코드는 runBlocking 함수를 사용하여 main 함수를 코루틴으로 변환합니다. launch 함수를 사용하여 코루틴을 시작하고 try-catch 블록으로 예외 처리를 수행합니다. finally 블록은 코루틴이 취소될 때 항상 실행됩니다. 1초 후에 job.cancel()을 호출하여 코루틴 실행을 취소합니다. join 함수를 사용하여 코루틴이 완료될 때까지 기다립니다. 마지막으로 main 함수가 종료됩니다.

### 코루틴 컨텍스트와 스코프
코루틴은 실행을 위해 컨텍스트를 필요로 합니다. 컨텍스트는 코루틴이 실행되는 환경을 정의합니다. 예를 들어, 코루틴이 어떤 스레드에서 실행될지, 코루틴이 실행 중인 동안 사용 가능한 변수와 자원은 무엇인지 등을 정의합니다.

코루틴 컨텍스트는 CoroutineContext 인터페이스를 구현합니다. CoroutineContext는 맵 형태로 구성되며, 각 요소는 CoroutineContext.Element를 구현해야 합니다.

코루틴 컨텍스트는 launch 함수와 async 함수에서 선택적으로 지정할 수 있습니다. 다음은 launch 함수에서 코루틴 컨텍스트를 지정하는 방법입니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch(Dispatchers.Default + CoroutineName("test")) {
        println("코루틴 실행 중")
    }
}
```
위 코드에서 launch 함수의 첫 번째 인자는 코루틴 컨텍스트를 나타냅니다. Dispatchers.Default는 기본적으로 코루틴을 실행할 스레드를 지정하며, CoroutineName은 코루틴의 이름을 지정합니다.

코루틴 컨텍스트는 불변성을 유지해야 합니다. 이는 기존의 컨텍스트에서 새로운 요소를 추가하거나 기존 요소를 변경할 수 없다는 것을 의미합니다. 따라서, 기존의 컨텍스트에서 새로운 컨텍스트를 생성하여 필요한 변경을 수행합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val context = Dispatchers.Default + CoroutineName("test")
    val newContext = context + Job()
    launch(newContext) {
        println("코루틴 실행 중")
    }
}
```
위 코드에서 + 연산자를 사용하여 기존의 컨텍스트에서 새로운 Job 요소를 추가합니다.

### 코루틴 디버깅
코루틴을 디버깅하는 것은 어렵습니다. 디버그 메시지와 함께 코루틴에서 예외가 발생하면 해당 예외를 처리하기 어려울 수 있습니다. 디버깅을 쉽게 하기 위해 kotlinx.coroutines.debug 패키지를 사용할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.debug.*

fun main() = runBlocking {
    val job = launch(CoroutineDebugInfo()) {
        try {
            println("코루틴 실행 중")
            delay(1000)
            throw Exception("코루틴 예외")
        } catch (e: Exception) {
            println("예외 처리: $e")
        }
    }
    delay(500)
    job.cancel()
}
```

CoroutineDebugInfo() 인스턴스를 launch 함수에 전달하여 코루틴 디버깅 정보를 추가합니다. 디버그 메시지와 함께 코루틴 예외가 발생하면 디버그 메시지를 사용하여 예외를 쉽게 추적할 수 있습니다.


### 비동기 코드 작성
코루틴은 비동기 코드 작성을 단순화합니다. async 함수를 사용하여 비동기 작업을 수행할 수 있습니다. async 함수는 Deferred<T> 인스턴스를 반환합니다. 이 인스턴스는 await() 함수를 사용하여 결과를 가져올 수 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val deferred = async {
        println("비동기 작업 실행 중")
        delay(1000)
        "결과"
    }
    println(deferred.await())
}
```
async 함수를 사용하여 비동기 작업을 실행합니다. delay 함수를 사용하여 1초 동안 작업을 수행합니다. await 함수를 사용하여 비동기 작업의 결과를 가져옵니다.

### withContext 함수
withContext 함수는 launch 함수와 유사하지만 코루틴에서 반환하는 값을 지정할 수 있습니다. withContext 함수를 사용하여 다른 스레드에서 작업을 수행한 다음 결과를 반환할 수 있습니다.

```kotlin
import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun main() = runBlocking {
    val executor = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    val result = withContext(executor) {
        println("다른 스레드에서 실행 중")
        "결과"
    }
    println(result)
}
```
withContext 함수를 사용하여 executor에서 코드 블록을 실행합니다. println 함수를 사용하여 다른 스레드에서 코드 블록이 실행됨을 확인합니다. 코드 블록에서 "결과" 문자열을 반환합니다. withContext 함수는 이 값을 반환합니다. 결과 문자열을 출력합니다.

### 코루틴 핸들링
launch 함수를 사용하여 코루틴을 시작하면 Job 인스턴스가 반환됩니다. Job 인스턴스를 사용하여 코루틴을 제어할 수 있습니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        println("코루틴 실행 중")
        delay(1000)
    }
    job.join()
    println("코루틴 완료")
}
```
launch 함수를 사용하여 코루틴을 시작하고 Job 인스턴스를 반환합니다. join() 함수를 사용하여 코루틴이 완료될 때까지 대기합니다. 그리고 "코루틴 완료" 메시지를 출력합니다.

코루틴을 취소하려면 cancel() 함수를 호출합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        try {
            println("코루틴 실행 중")
            delay(1000)
        } finally {
            println("코루틴 완료")
        }
    }
    delay(500)
    job.cancel()
}
```
launch 함수를 사용하여 코루틴을 시작합니다. delay 함수를 사용하여 1초 동안 작업을 수행합니다. finally 블록에서 "코루틴 완료" 메시지를 출력합니다. 코루틴이 취소되면 finally 블록이 실행됩니다.

코루틴이 취소되면 CancellationException 예외가 발생합니다. 이 예외를 처리하려면 try-catch 블록을 사용합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        try {
            println("코루틴 실행 중")
            delay(1000)
        } catch (e: CancellationException) {
            println("코루틴 취소됨")
        } finally {
            println("코루틴 완료")
        }
    }
    delay(500)
    job.cancel()
}
```
try-catch 블록을 사용하여 CancellationException 예외를 처리합니다. "코루틴 취소됨" 메시지를 출력합니다.

### 코루틴 빌더
코루틴을 시작하는 방법 중 하나는 코루틴 빌더(coroutine builder)를 사용하는 것입니다. 코루틴 빌더는 launch, async, runBlocking, withContext 등의 함수를 제공합니다.

*launch*
    
launch 함수는 비동기 코루틴을 실행하고, Job 인스턴스를 반환합니다. launch 함수는 실행이 완료될 때까지 기다리지 않습니다.

```kotlin
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}
```
이 코드는 "Hello,"와 "World!"를 출력합니다. runBlocking 함수는 메인 스레드를 차단하며, launch 함수를 통해 생성된 코루틴은 지정된 시간만큼 지연되고 "World!"를 출력합니다.

*async와 await*
    
async 함수는 비동기 코루틴을 실행하고, Deferred 인스턴스를 반환합니다. Deferred 인스턴스는 await 함수를 사용하여 비동기 작업의 결과를 반환합니다.

```kotlin
fun main() = runBlocking {
    val deferred = async {
        delay(1000L)
        "World!"
    }
    println("Hello, ${deferred.await()}")
}
```
이 코드는 "Hello, World!"를 출력합니다. async 함수를 사용하여 Deferred 인스턴스를 생성하고, await 함수를 사용하여 해당 작업이 완료될 때까지 기다립니다. await 함수는 결과 값을 반환합니다.

*runBlocking*
    
runBlocking 함수는 지정된 블록을 실행하면서 메인 스레드를 차단합니다. runBlocking 함수는 코루틴 빌더 함수가 아닌 일반 함수에서 사용할 수 있습니다.

``` kotlin
fun main() = runBlocking {
    println("Hello,")
    delay(1000L)
    println("World!")
}
```
이 코드는 "Hello,"와 "World!"를 출력합니다. runBlocking 함수는 지정된 블록을 실행하면서 메인 스레드를 차단합니다. delay 함수는 비동기적으로 작동합니다.

*withContext*
    
withContext 함수는 지정된 코루틴 컨텍스트에서 코드를 실행합니다.

```kotlin
suspend fun getUserData(userId: String): String {
    return withContext(Dispatchers.IO) {
        URL("https://example.com/users/$userId").readText()
    }
}
```
이 코드는 Dispatchers.IO 코루틴 컨텍스트에서 지정된 URL에서 데이터를 읽고, 해당 데이터를 반환합니다. withContext 함수는 실행 중인 스레드를 차단하지 않습니다. 대신, Dispatchers.IO에서 새로운 코루틴을 시작하여 작업을 처리합니다.
