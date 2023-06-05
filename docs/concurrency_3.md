# 라이프 사이클과 에러 핸들링

> job and deferred

### 비동기 함수의 그룹

> 해당 작업에 접근하고 예외가 발생하면 그에 대응하거나, 해당 작업이 더 이상 필요하지 않을 때는 취소

- 결과가 없는 비동기 함수 : 완료 여부를 모니터링할 수 있지만 결과를 갖지 않는 백그라운드 작업
- 결과를 반환하는 비동기 함수 : 함수를 사용해 정보를 반환

### 잡 JOB

- 파이어 앤 포갯 작업
- 한번 시작된 작업은 예외가 발생하지 않는 한 대기하지 않음

```kotlin
// JobSupport의 구현체
val job = GlobalScope.launch {
}

val job = Job()
```

### Job의 예외처리

- 잡 내무에서 발생하는 예외는 잡을 생성한 곳까지 전파
- 잡이 완료되기를 기다리지 않아도 발생

### Job의 라이프 사이클

> 잡이 특정 상태에 도달하면, 이전 상태로 되돌아가지 않음, 최종상태(취소됨, 완료됨)인 경우, 옮길 수 없는 

- New(생성) : 존재하지만 아직 실행되지 않는 잡
- Active(활성) : 실행 중인 잡. 일시 중단된 잡도 활성으로 간주
- Completed(완료 됨) : 잡이 더 이상 실행되지 않는 경우
- Canceling(취소 중) : 실행 중인 잡에서 cancel()이 호출되면 취소가 완료될 때까지 시간이 걸림, 활성과 취소 사이의 중간 상태
- Cancelled(취소 됨) : 취소로 인해 실행이 완료된 잡, 취소된 잡도 완료로 간주

### 생성 (new)

- 기본적으로 launch() or job()을 사용해 생성될 때 자동으로 시작
- CoroutineStart.LAZY를 통해 자동으로 시작되지 않게 할 수 있음

```kotlin
GlobalScope.launch(start = CoroutineStart.LAZY){
}
```

### 활성 (active)

- start() : 잡이 완료될 때까지 기다리지 않고 호출
  - 실행을 일시 중단하지 않으므로, 애플리케이션의 어느 부분에서든 실행 가능 
- join() : 잡이 완료될때 까지 실행을 일시중단
  - 코루틴 혹은 일시중단 함수에서 호출해야 함

### 취소 중 (cacelling)

- 잡에 실행을 취소하는 중, cancel() 호출
- cancelAndJoin() : 실행을 취소할 뿐만 아니라 취소가 완료될 때까지 현재 코루틴을 일시중단

### 취소됨 (cancelled)

- 취소 또는 처리되지 않은 예외로 인해 실행이 종료된 잡은 취소됨으로 간주
- getCancellationException()을 통해 취소 정보 파악 가능, 이슈 체킹 가능

취소된 잡과 예외 구분을 위해 CoroutinExceptionHandler 설정을 하면 좋음

```kotlin
val exceptionHandler = CoroutinExceptionHandler {
  _: CoroutineContext, throwable: Throwable ->
    logger.error(e)
}

GlobalScope.launch(exceptionHandler) {
  // TODO
}
```

아니면 invokeOnCompletion 사용

```kotlin
GlobalScope.launch{
}.invokeOnCompletion { cause ->
  cause?.let {
    // TODO
  }
}
```

### 완료됨 (completed)

- 실행이 중지된 잡은 완료됨으로 간주
- 실행이 정상적으로 종료됐거나, 취소됐는지 또는 예외 때문에 종료됐는지 여부에 관계없이 적용
- 취소된 항목은 완료된 항목의 하위 항목으로 간주

### 잡의 현재 상태 확인

- isActive : 잡이 활성상태인 여부
- isCompleted : 잡이 실행을 완료했는지
- isCancelled : 잡 취소 여부, 취소가 요청되면 즉시 true

### 디퍼드 (Deferred)

- 결과를 갖는 비동기 작업을 수행하기 위해 잡을 확장
- 퓨처 또는 프로미스와 동의어, 디퍼드는 코틀린 구현체
- 연산이 객체를 반환할 것이며, 객체는 비동기 작업이 완료될 때까지 비어 있다는 것

### 예외 처리

- 순수한 잡과 달리 디퍼드는 처리되지 않은 예외를 자동으로 전파하지 않음
- 디퍼드의 결과를 대기할 것으로 예상하기 때문
- 실행이 성공했는지 확인하는 것은 

### 정리

- 잡은 아무것도 반환하지 않는 작업에 사용
- 디퍼드는 작업 내용을 반환하여 사용하는 경우 사용
- 디퍼드와 잡의 상태는 같음
- 잡의 예외처리 중요
