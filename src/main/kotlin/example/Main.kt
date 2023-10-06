package example

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
