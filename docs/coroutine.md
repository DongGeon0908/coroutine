# 코루틴 정리

### 루틴과 코루틴의 차이

**루틴**

- 루틴에 진입하는 곳은 한 군데이며, 종료되면 해당 루틴의 정보가 초기화
- 시작되면 끝날 떄 까지 멈추지 않음
- 한 번 끝나면 루틴 내의 정보가 사라짐

```kotlin
fun a() {
    println("hello")
    b()
    println("hello")
}

fun b() {
    val a = "hello"
    println(a)
}
```

**코루틴**

- 중단되었다가 재개될 수 있음
- 중단되더라도 루틴 내의 정보가 사라지지 않는다.

```kotlin
fun main(): Unit {
    return runBlocking {
        println("start")

        launch { newRoutine() }

        yield()

        printlnWithThread("end")
    }
}

suspend fun newRoutine() {
    val num1 = 1
    val num2 = 2

    // 현재 코루틴을 중단하고, 다른 코루틴이 실행되도록 함
    yield()

    printlnWithThread(num1 + num2)
}

fun printlnWithThread(param: Any) {
    println("[${Thread.currentThread().name}] $param")
}
```

**코루틴 디버깅 옵션**

```kotlin
VM OPTIONS
        -Dkotlinx.coroutines.debug
```

### 코루틴과 스레드의 차이

- 스레드는 프로세스에 종속적이지만, 코루틴은 종속 관계가 아님
    - 스레드는 특정 프로세스에 종속
    - 코루틴은 특정 스레드에 종속되지 않음
- os가 스레드를 강제로 멈추고, 다른 스레드를 실행
- 코루틴 자체적으로 다른 코루틴에게 작업 진행을 양보

### Context Switching에서의 차이

- 프로세스의 context-switching시 모든 메모리가 변경이 되어짐 (비용 많이 발생)
- 스레드의 context-switching의 경우 Heap 메모리는 공유하고, stack만 교체되므로 비용이 상대적으로 적음
- 코루틴의 context-switching의 경우, 동일 스레드에서 코루틴이 실행되면 메모리 전부를 공유하므로, 스레드의 context-switching보다 비용이 적음
    - 하나의 스레드 안에서 동시성을 확보할 수 있다.

### Coroutine Start Options

- DEFAULT : immediately schedules coroutine for execution according to its context;
- LAZY : starts coroutine lazily, only when it is needed;
- ATOMIC : atomically (in a non-cancellable way) schedules coroutine for execution according to its context;
- UNDISPATCHED : immediately executes coroutine until its first suspension point in the current thread.

### Coroutine Cancel

- isActive : 현재 코루틴이 활성화 되어 있는지, 취소 신호를 받았는지
- Dispatchers.Default : 우리의 코루틴을 다른 스레드에 배정
