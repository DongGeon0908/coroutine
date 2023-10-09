package example

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

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

fun launchJob(): Unit = runBlocking {
    // CoroutineStart.LAZY 대기하다가, start 호출시 진행
    val job = launch(start = CoroutineStart.LAZY) {
        printlnWithThread("HELLO")
    }

    delay(1000)
    job.start()
}

fun stopLaunchJob(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printlnWithThread(it)
            delay(500)
        }
    }

    delay(1000)

    // 코루틴 멈추기
    job.cancel()
}

// 코루틴 빌더, launch
// launch.start : 시작신호
// launch.cancel : 취소신호
// launch.join() : 코루틴이 완료될 때까지 대기


// 코루틴 빌더, async
fun asyncJob() = runBlocking {
    val job = async { 3 + 5 }

    val result = job.await() // async의 결과를 가져옴
}
