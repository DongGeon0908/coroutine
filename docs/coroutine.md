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
