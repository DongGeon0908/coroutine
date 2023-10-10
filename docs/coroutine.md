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

### launch와 async의 예외 발생 차이

- launch : 예외가 발생하면, 예외를 출력하고 코루틴 종료
- async : 예외가 발생해도 예외를 출력하지 않으며, 확인할려면 await이 필요

### 코루틴의 예외전파

- 자식 코루틴의 예외는 부모 코루틴으로 전파되어진다.

```kotlin
/**
 * Creates a _supervisor_ job object in an active state.
 * Children of a supervisor job can fail independently of each other.
 *
 * A failure or cancellation of a child does not cause the supervisor job to fail and does not affect its other children,
 * so a supervisor can implement a custom policy for handling failures of its children:
 *
 * * A failure of a child job that was created using [launch][CoroutineScope.launch] can be handled via [CoroutineExceptionHandler] in the context.
 * * A failure of a child job that was created using [async][CoroutineScope.async] can be handled via [Deferred.await] on the resulting deferred value.
 *
 * If [parent] job is specified, then this supervisor job becomes a child job of its parent and is cancelled when its
 * parent fails or is cancelled. All this supervisor's children are cancelled in this case, too. The invocation of
 * [cancel][Job.cancel] with exception (other than [CancellationException]) on this supervisor job also cancels parent.
 *
 * @param parent an optional parent job.
 */
@Suppress("FunctionName")
public fun SupervisorJob(parent: Job? = null): CompletableJob = SupervisorJobImpl(parent)
```

### CoroutineExceptionHandler

- launch에만 적용 가능
- 부모 코루틴 존재시 동작하지 않는다.
- 발생한 예외가 CancellationException인 경우, 취소로 간주하고 부모 코루틴에게 전파하지 않는다.
- 그 외 다른 예외가 발생한 경우, 실패로 간주하고 부모 코루틴에게 전파한다.

```kotlin
internal expect fun handleCoroutineExceptionImpl(context: CoroutineContext, exception: Throwable)

/**
 * Helper function for coroutine builder implementations to handle uncaught and unexpected exceptions in coroutines,
 * that could not be otherwise handled in a normal way through structured concurrency, saving them to a future, and
 * cannot be rethrown. This is a last resort handler to prevent lost exceptions.
 *
 * If there is [CoroutineExceptionHandler] in the context, then it is used. If it throws an exception during handling
 * or is absent, all instances of [CoroutineExceptionHandler] found via [ServiceLoader] and
 * [Thread.uncaughtExceptionHandler] are invoked.
 */
@InternalCoroutinesApi
public fun handleCoroutineException(context: CoroutineContext, exception: Throwable) {
    // Invoke an exception handler from the context if present
    try {
        context[CoroutineExceptionHandler]?.let {
            it.handleException(context, exception)
            return
        }
    } catch (t: Throwable) {
        handleCoroutineExceptionImpl(context, handlerException(exception, t))
        return
    }
    // If a handler is not present in the context or an exception was thrown, fallback to the global handler
    handleCoroutineExceptionImpl(context, exception)
}

internal fun handlerException(originalException: Throwable, thrownException: Throwable): Throwable {
    if (originalException === thrownException) return originalException
    return RuntimeException("Exception while trying to handle coroutine exception", thrownException).apply {
        addSuppressedThrowable(originalException)
    }
}

/**
 * Creates a [CoroutineExceptionHandler] instance.
 * @param handler a function which handles exception thrown by a coroutine
 */
@Suppress("FunctionName")
public inline fun CoroutineExceptionHandler(crossinline handler: (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler =
    object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) =
            handler.invoke(context, exception)
    }

/**
 * An optional element in the coroutine context to handle **uncaught** exceptions.
 *
 * Normally, uncaught exceptions can only result from _root_ coroutines created using the [launch][CoroutineScope.launch] builder.
 * All _children_ coroutines (coroutines created in the context of another [Job]) delegate handling of their
 * exceptions to their parent coroutine, which also delegates to the parent, and so on until the root,
 * so the `CoroutineExceptionHandler` installed in their context is never used.
 * Coroutines running with [SupervisorJob] do not propagate exceptions to their parent and are treated like root coroutines.
 * A coroutine that was created using [async][CoroutineScope.async] always catches all its exceptions and represents them
 * in the resulting [Deferred] object, so it cannot result in uncaught exceptions.
 *
 * ### Handling coroutine exceptions
 *
 * `CoroutineExceptionHandler` is a last-resort mechanism for global "catch all" behavior.
 * You cannot recover from the exception in the `CoroutineExceptionHandler`. The coroutine had already completed
 * with the corresponding exception when the handler is called. Normally, the handler is used to
 * log the exception, show some kind of error message, terminate, and/or restart the application.
 *
 * If you need to handle exception in a specific part of the code, it is recommended to use `try`/`catch` around
 * the corresponding code inside your coroutine. This way you can prevent completion of the coroutine
 * with the exception (exception is now _caught_), retry the operation, and/or take other arbitrary actions:
 *
 * ```

* scope.launch { // launch child coroutine in a scope
*     try {
*          // do something
*     } catch (e: Throwable) {
*          // handle exception
*     }
* }
* ```
*
* ### Implementation details
*
* By default, when no handler is installed, uncaught exception are handled in the following way:
*
    * If exception is [CancellationException] then it is ignored
* (because that is the supposed mechanism to cancel the running coroutine)
*
    * Otherwise:
*     * if there is a [Job] in the context, then [Job.cancel] is invoked;
*     * Otherwise, all instances of [CoroutineExceptionHandler] found via [ServiceLoader]
*     * and current thread's [Thread.uncaughtExceptionHandler] are invoked.
*
* [CoroutineExceptionHandler] can be invoked from an arbitrary thread.
  */ public interface CoroutineExceptionHandler : CoroutineContext.Element { /**
    * Key for [CoroutineExceptionHandler] instance in the coroutine context.
      */ public companion object Key : CoroutineContext.Key<CoroutineExceptionHandler>

  /**
    * Handles uncaught [exception] in the given [context]. It is invoked
    * if coroutine has an uncaught exception.
      */ public fun handleException(context: CoroutineContext, exception: Throwable)
      }

```

### Structured Concurrency

- 부모 코루틴과 자식 코루틴은 항상 같이 다님
- 수많은 코루틴이 유실되거나 누수되지 않도록 보장
- 코루틴이 tree 구조로 구성되어 있음
- 자식 코루틴에서 예외가 발생할 경우, Structured Concurrency에 의해 부모 코루틴이 취소되고, 부모 코루틴이 다른 자식 코루틴을 취소시킴
- 부모 코루틴이 취소되면, 자식 코루틴도 취소됨
- 코드 내의 에러가 유실되지 않고 적절히 보고될 수 있도록 보장
- CancellationException은 정상적인 취소로 간주하며, 부모 코루틴에서 전파되지 않고, 다른 자식 코루틴도 취소시키지 않음

```kotlin
Coroutines follow a principle of structured concurrency which means that new coroutines can only be launched in a specific CoroutineScope which delimits the lifetime of the coroutine.The above example shows that runBlocking establishes the corresponding scope and that is why the previous example waits until World !is printed after a second's delay and only then exits.

In a real application, you will be launching a lot of coroutines . Structured concurrency ensures that they are not lost and do not leak . An outer scope cannot complete until all its children coroutines complete . Structured concurrency also ensures that any errors in the code are properly reported and are never lost .
```

### CoroutineScope and CoroutineContext

- coroutine은 coroutineScope안에서 동작 (async, launch)
- coroutineScope는 coroutineContext라는 데이터를 보관
- coroutineScope는 코루틴이 탄생할 수 있는 영역
- coroutineContext는 현재 코루틴의 이름, coroutineExceptionHandler, Job, CoroutineDispatchers 등의 정보들이 들어 있다.
- coroutineContext는 key, value 형태로 데이터를 저장

```kotlin
package kotlin.coroutines

/**
 * Persistent context for the coroutine. It is an indexed set of [Element] instances.
 * An indexed set is a mix between a set and a map.
 * Every element in this set has a unique [Key].
 */
@SinceKotlin("1.3")
public interface CoroutineContext {
    /**
     * Returns the element with the given [key] from this context or `null`.
     */
    public operator fun <E : Element> get(key: Key<E>): E?

    /**
     * Accumulates entries of this context starting with [initial] value and applying [operation]
     * from left to right to current accumulator value and each element of this context.
     */
    public fun <R> fold(initial: R, operation: (R, Element) -> R): R

    /**
     * Returns a context containing elements from this context and elements from  other [context].
     * The elements from this context with the same key as in the other one are dropped.
     */
    public operator fun plus(context: CoroutineContext): CoroutineContext =
        if (context === EmptyCoroutineContext) this else // fast path -- avoid lambda creation
            context.fold(this) { acc, element ->
                val removed = acc.minusKey(element.key)
                if (removed === EmptyCoroutineContext) element else {
                    // make sure interceptor is always last in the context (and thus is fast to get when present)
                    val interceptor = removed[ContinuationInterceptor]
                    if (interceptor == null) CombinedContext(removed, element) else {
                        val left = removed.minusKey(ContinuationInterceptor)
                        if (left === EmptyCoroutineContext) CombinedContext(element, interceptor) else
                            CombinedContext(CombinedContext(left, element), interceptor)
                    }
                }
            }

    /**
     * Returns a context containing elements from this context, but without an element with
     * the specified [key].
     */
    public fun minusKey(key: Key<*>): CoroutineContext

    /**
     * Key for the elements of [CoroutineContext]. [E] is a type of element with this key.
     */
    public interface Key<E : Element>

    /**
     * An element of the [CoroutineContext]. An element of the coroutine context is a singleton context by itself.
     */
    public interface Element : CoroutineContext {
        /**
         * A key of this coroutine context element.
         */
        public val key: Key<*>

        public override operator fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as E else null

        public override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        public override fun minusKey(key: Key<*>): CoroutineContext =
            if (this.key == key) EmptyCoroutineContext else this
    }
}
```

### DisPatcher

- coroutine이 어떤 스레드에 배정될지를 관리
- ExecutorService.asCoroutineDispatcher() : ExecutorService를 디스패처로 전환 가능한 extensions

### Suspend

- 중지되었다가, 다시 재개될 수 있는 메서드
```kotlin
fun main(): Unit = runBlocking {
  val result1 = apiCall1()
  val result2 = apiCall2(result1)
  printWithThread(result2)
}
suspend fun apiCall1(): Int {
  return CoroutineScope(Dispatchers.Default).async {
    Thread.sleep(1_000L)
    100
  }.await()
}
suspend fun apiCall2(num: Int): Int {
  return CompletableFuture.supplyAsync {
    Thread.sleep(1_000L)
    100
  }.await()
}
```

### Suspend Function

- coroutineScope : launch or async와 같은 새로운 코루틴을 만들지만, 주어진 함수 블록이 바로 실행됨, 새로 생긴 코루틴과 자식 코루틴들이 모두 완료된 이후, 반환. coroutineScope로 만든 코루틴은 이전 코루틴의 자식 코루틴이 된다.
- withContext : 주어진 코드 블록이 즉시 호출되며 새로운 코루틴이 생성, 코루틴이 완전히 종료되어야 반환, 기본적으로 coroutineScope와 동일, 대신 다른점은 context에 변화를 줄 수 있음 ex) withContext(Dispatcher.IO){}
- withTimeout : 주어진 함수 블록이 시간 내에 완료되어야 함, 완료되지 않으면 TimeoutCancellationException 발생
- withTimeoutNull : 주어진 함수 블록이 시간 내에 완료되어야 함, 완료되지 않으면 null 반환
