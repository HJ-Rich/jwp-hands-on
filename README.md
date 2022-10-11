# JWP Hands-On

## 만들면서 배우는 스프링 실습 코드

### 학습 순서

- cache
- thread
- servlet
- reflection
- di

## Stage 0 - 트랜잭션의 4가지 속성

- ACID
- A : Atomic - 트랜잭션이 완전히 성공하거나 완전히 실패하는 단일 단위로 처리되도록 보장
- C : Consistency - 트랜잭션이 성공적으로 완료하면 언제나 동일한 데이터베이스 상태로 유지
- I : Isolation - 동시에 실행되는 트랜잭션이 서로에게 영향을 미치지 못하도록 보장
- D : Durability - 트랜잭션을 성공적으로 실행하면 그 결과가 항상 기록

<br><br>

## Stage 1 - 트랜잭션 격리 수준

- Read Uncommitted : 다른 트랜잭션에서 만든 커밋 이전의 변경 내역까지 조회함. Dirty Read 발생
- Read Committed : 다른 트랜잭션에서 커밋한 변경 내역을 조회함. 같은 트랜잭션 내에서도 조회할 때마다 결과가 달라질 수 있음. 한 행의 값이 바뀌면 Non-Repeatable Read, 행의 수가
  달라지면 Phantom Read
- Repeatable Read : 한 트랜잭션 내에서 같은 행은 반복해서 조회해도 같은 결과를 보장. Non-Repeatable Read 발생하지 않음. 그러나 조건식에 따른 행의 수가 달라질 수 있음.
  Phantom Read 발생. 단, InnoDB를 기본 엔진으로 사용하는 MySQL의 경우, Repeatable Read 수준에서도 Phantom Read가 발생하지 않음.
- Serializable : Phanntom Read도 발생하지 않는 최고 수준의 격리 레벨.

<br><br>

## Stage 2 - 트랜잭션 전파 옵션

- REQUIRED
    - 기본 옵션
    - 기존 트랜잭션이 있으면 합류, 없으면 생성
    - REQUIRED + REQUIRED => 합류하므로 하나의 트랜잭션만 열림
- REQUIRED_NEW
    - 기존 트랜잭션에 독립적인 새로운 트랜잭션을 시작.
    - REQUIRED + REQUIRED_NEW => 2개의 트랜잭션이 열림
    - REQUIRED와 REQUIRED_NEW는 상호간에 예외 발생 시 서로 롤백에 영향을 미치지 않음
- SUPPORT
    - 기존 트랜잭션이 있으면 참여, 없으면 트랜잭션 없이 수행
    - REQUIRED + SUPPORT => 1개의 트랜잭션 내에 합류되어 수행
- MANDATORY
    - 기존 트랜잭션이 있으면 참여, 없으면 예외 던짐 IllegalTransactionStateException
- NOT_SUPPORTED
    - 기존 트랜잭션이 있으면 중단시키고 트랜잭선 없이 수행.
- NESTED
    - 기존 트랜잭션이 있으면 세이브 포인트로 동작, 없으면 REQUIRED 처럼 동작
    - REQUIRED(정상) + NESTED(예외발생) => NESTED 내 처리만 롤백되고 REQUIRED는 정상 반영
    - REQUIRED(예외발생) + NESTED(정상) => 모두 롤백됨. NESTED는 부모 트랜잭션 커밋 시 커밋됨
- NEVER
    - 기존 트랜잭션이 있으면 예외 발생, 트랜잭션 없이 수행

<br><br>
