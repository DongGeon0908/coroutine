/*
 * 2016-2021 JetBrains s.r.o.에 의해 저작권이 보호됩니다. 이 소스 코드의 사용은 Apache 2.0 라이센스에 따릅니다.
 */

@file:Suppress("unused")

// package kotlinx.coroutines

import kotlinx.coroutines.internal.*
import kotlinx.coroutines.scheduling.*
import kotlin.coroutines.*

/**
 * [CoroutineDispatcher]의 다양한 구현을 그룹화한 객체입니다.
 */
public actual object Dispatchers {
    /**
     * 모든 표준 빌더([launch][CoroutineScope.launch], [async][CoroutineScope.async] 등)에서 사용되는
     * 기본 [CoroutineDispatcher]입니다.
     * 만약 해당 컨텍스트에 디스패처나 다른 [ContinuationInterceptor]가 지정되지 않은 경우에 사용됩니다.
     *
     * 이 디스패처는 JVM에서 공유 스레드 풀을 기반으로 합니다. 기본적으로 이 디스패처에서 사용되는
     * 병렬 실행 수준은 CPU 코어의 수와 동일하지만 최소한 두 개입니다.
     * 병렬 실행 수준 X는 이 디스패처에서 최대 X개의 작업이 동시에 실행되지 않음을 보장합니다.
     */
    @JvmStatic
    public actual val Default: CoroutineDispatcher = DefaultScheduler

    /**
     * UI 객체와 상호 작용하는 Main 스레드에 국한된 코루틴 디스패처입니다.
     * 이 디스패처는 직접적으로 또는 [MainScope] 팩토리를 통해 사용할 수 있습니다.
     * 보통 이러한 디스패처는 단일 스레드입니다.
     *
     * 이 속성에 접근하면 클래스패스에 메인 스레드 디스패처가 없는 경우 [IllegalStateException]을 throw할 수 있습니다.
     *
     * 플랫폼 및 클래스패스에 따라 다른 디스패처로 매핑될 수 있습니다.
     * - JS 및 Native에서는 [Default] 디스패처와 동등합니다.
     * - JVM에서는 Android 메인 스레드 디스패처, JavaFx 또는 Swing EDT 디스패처 중 하나입니다.
     *   이는 [`ServiceLoader`](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)에 의해 선택됩니다.
     *
     * `Main` 디스패처로 작업하려면 프로젝트 런타임 종속성에 다음 아티팩트를 추가해야 합니다:
     *  - Android 메인 스레드 디스패처용 `kotlinx-coroutines-android`
     *  - JavaFx 애플리케이션 스레드 디스패처용 `kotlinx-coroutines-javafx`
     *  - Swing EDT 디스패처용 `kotlinx-coroutines-swing`
     *
     * 테스트 목적으로 사용자 정의 `Main` 디스패처를 설정하려면 `kotlinx-coroutines-test` 아티팩트를
     * 프로젝트 테스트 종속성에 추가하십시오.
     *
     * 구현 참고: [MainCoroutineDispatcher.immediate]는 Native 및 JS 플랫폼에서 지원되지 않습니다.
     */
    @JvmStatic
    public actual val Main: MainCoroutineDispatcher get() = MainDispatcherLoader.dispatcher

    /**
     * 특정 스레드에 국한되지 않는 코루틴 디스패처입니다.
     * 이 디스패처는 코루틴의 초기 계속을 현재 호출 프레임에서 실행하고,
     * 해당 지연 함수에 의해 사용되는 스레드가 어떤 것이든 스레드에서 코루틴을 다시 시작합니다.
     * 특정 스레딩 정책을 강제하지 않으며, 이 디스패처에서 시작된 중첩 코루틴은 스택 오버플로우를 피하기 위해
     * 이벤트 루프를 형성합니다.
     *
     * ### 이벤트 루프
     * 이벤트 루프 의미론은 순수한 내부 개념이며 실행 순서에 대한 어떠한 보장도 없습니다.
     * 다음과 같은 코드:
     * ```
     * withContext(Dispatchers.Unconfined) {
     *    println(1)
     *    withContext(Dispatchers.Unconfined) { // 중첩된 스레드가 없음
     *        println(2)
     *    }
     *    println(3)
     * }
     * println("Done")
     * ```
     * "1 2 3"과 "1 3 2"를 출력할 수 있으며 이것은 변경될 수 있는 구현 세부사항입니다.
     * 그러나 "Done"은 두 `withContext`가 모두 완료될 때만 출력됩니다.
     *
     * 현재 계속이 발생한 후에도 코루틴을 특정 스레드 또는 스레드 풀에 제한하지 않고,
     * 첫 번째 지연까지 현재 호출 프레임에서 실행하려면 [CoroutineStart] 매개변수를
     * [CoroutineStart.UNDISPATCHED] 값으로 설정하여 [launch][CoroutineScope.launch] 및
     * [async][CoroutineScope.async]와 같은 코루틴 빌더를 사용할 수 있습니다.
     */
    @JvmStatic
    public actual val Unconfined: CoroutineDispatcher = kotlinx.coroutines.Unconfined

    /**
     * 블로킹 IO 작업을 공유 스레드 풀로 오프로드하는 데 사용되는 코루틴 디스패처입니다.
     *
     * 이 디스패처 내에서 작업에 사용되는 스레드 수는
     * "`kotlinx.coroutines.io.parallelism`" ([IO_PARALLELISM_PROPERTY_NAME]) 시스템 프로퍼티의 값에 의해 제한됩니다.
     * 기본값은 64개 스레드 또는 코어 수 중 더 큰 값입니다.
     *
     * ### 제한된 병렬성을 위한 탄력성
     *
     * `Dispatchers.IO`는 탄력성의 독특한 속성을 가지고 있습니다. [CoroutineDispatcher.limitedParallelism]을 통해 얻은 뷰는
     * `Dispatchers.IO` 병렬성 제한에 따르지 않습니다. 개념적으로는 무제한 스레드 풀을 사용하는 디스패처의 뷰가 있으며,
     * `Dispatchers.IO` 및 `Dispatchers.IO`의 뷰는 사실 같은 디스패처의 뷰입니다. 실제로는 `Dispatchers.IO`와
     * 그 뷰가 스레드 및 리소스를 공유합니다.
     *
     * 다음 예제에서
     * ```
     * // MySQL 연결에 100개 스레드 사용
     * val myMysqlDbDispatcher = Dispatchers.IO.limitedParallelism(100)
     * // MongoDB 연결에 60개 스레드 사용
     * val myMongoDbDispatcher = Dispatchers.IO.limitedParallelism(60)
     * ```
     * 시스템은 피크 로드 중에 블로킹 작업에 대한 최대 `64 + 100 + 60` 개의 스레드를 가질 수 있지만,
     * 안정 상태에서는 `Dispatchers.IO`, `myMysqlDbDispatcher` 및 `myMongoDbDispatcher` 사이에서
     * 작은 수의 스레드만 공유됩니다.
     *
     * ### 구현 참고
     *
     * 이 디스패처 및 이 디스패처의 뷰는 [Default][Dispatchers.Default] 디스패처와 스레드를 공유하므로,
     * 이미 [Default][Dispatchers.Default] 디스패처에서 실행 중인 상황에서 `withContext(Dispatchers.IO)`를 사용하는 것은
     * 일반적으로 실제로 다른 스레드로의 전환을 의미하지 않습니다. 이러한 시나리오에서는 백그라운드 스레드가 생성되어
     * 사용되지 않는 경우에도 64(기본 병렬성)보다 많은 스레드가 생성될 수 있습니다.
     */
    @JvmStatic
    public val IO: CoroutineDispatcher = DefaultIoScheduler

    /**
     * 내장된 디스패처인 [Default] 및 [IO] 등을 종료하고, 해당 디스패처와 연결된 모든 스레드를 종료하며,
     * 모든 새 작업을 거부하도록 만듭니다.
     * 시간 관련 작업(예: `delay`, `withTimeout`) 및 다른 디스패처에서 거부된 작업을 처리하는
     * 백업 디스패처도 종료됩니다.
     *
     * 이것은 **신중하게** 사용해야 하는 API입니다. 이 API를 일반 애플리케이션 수준 코드에서 호출하지 말아야 하며,
     * 그 호출은 되돌릴 수 없습니다. 셧다운 호출은 대부분의 코루틴 프레임워크를 오퍼레이션할 수 없는 상태로 만들기 위한
     * 것이며, 셧다운 호출은 보류 중인 작업이나 활성 코루틴이 없는 경우에만 수행해야 합니다.
     * 그렇지 않으면 동작은 정의되지 않습니다. `shutdown`의 호출은 완료되지 않거나 실행되지 않는
     * 남은 작업을 남길 수 있거나, 예외를 throw할 수 있으며, 남은 작업은 영원히 완료되거나 실행되지 않을 수 있습니다.
     *
     * 셧다운의 주요 목표는 코루틴 프레임워크와 연결된 모든 백그라운드 스레드를 중지하여
     * Java 가상 머신에서 kotlinx.coroutines 클래스를 언로드 가능하게 만드는 것입니다.
     * 이것은 컨테이너화된 환경(OSGi, Gradle 플러그인 시스템, IDEA 플러그인)에서만 사용하는 것이 권장됩니다.
     */
    @DelicateCoroutinesApi
    public fun shutdown() {
        DefaultExecutor.shutdown()
        // Dispatchers.IO도 종료합니다.
        DefaultScheduler.shutdown()
    }
}
