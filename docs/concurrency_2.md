# 코루틴 인 액션

### CoroutineDispatcher

- 코틀린은 스레드와 스레드 풀을 쉽게 생성할 수 있지만, 직접 액세스하거나 제어하지 않음
- 가용성, 부하, 설정을 기반으로 스레드 간에 코루틴을 분산하는 오케스트레이터 (자동화 관리 도구)
- 코루틴을 특정 스레드 또는 스레드 그룹에서 실행하도록 할 수 있음

### async 코루틴

- 결과처리르 위한 목적의 코루틴
- Deferred<T> 반환, 디퍼드 코루틴 프레임워크에서 제공하는 취소 불가능한 넌블로킹 퓨처를 의미
- 결과 처리가 꼭 필요

### async 코루틴의 예외처리

- 블록안에서 발생한 예외를 결과에 첨부
- isCancelled와 getCancellationException을 통해 확인 가능
- 예외를 외부로 전파하기 위해서는 await() 호출 필요
- Join()은 예외를 전파하지 않고 처리, await은 호출만으로 예외 전파 가능



### launch 코루틴

- 결과를 반환하지 않는 코루틴
- 연산이 실패한 경우에만 통보 받기를 원하는 파이어-앤-포겟 시나리오에서 사용
- 필요할 때 취소 가능



### 코루틴을 다루는 옵션

- 코루틴으로 감싼 동기 함수
  - 명시적, 하지만 복잡성 높음
- 특정 디스패처를 갖는 비동기 함수
  - 내용 간단, 디스패처를 바꾸지 못해 유연성 적음
- 유연한 디스패처를 갖는 비동기 함수
  - 유연성 높음, 하지만 옵션이 많음



### 상황별 코루틴 사용

- 플랫폼 제약이 있는가?
  - 네트워킹을 할 때 코드가 잘못된 스레드에서 호출을 시도하지 않도록 비동기 함수를 사용하는 것이 유용
- 함수가 여러 곳에서 호출되는가?
  - launch() or async() 블록으로 동기 함수를 감싸는 것이 좋음
  - 명시적 가독성을 통한 동시성 파악 쉬움
- 함수 호출자가 어떤 디스패처를 사용할지 결정하기 원하는가?
  - 특정 디스패처를 강제할 것인지 혹은 유연하게 가져갈 것인지 파악



동시코드를 작성하는 방법에는 여러 방법이 있지만, 명확하고 안전하며 일관성 있게 코틀린의 유연성을 최대한 활용하는 방법을 이해하는 것이 중요