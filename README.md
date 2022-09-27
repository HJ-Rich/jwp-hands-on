# 📖 DI 컨테이너 구현하기

## 학습 목표

- 스프링 프레임워크의 핵심 기술인 DI 컨테이너를 구현한다.
- DI 컨테이너를 직접 구현하면서 스프링 DI에 대한 이해도를 높인다.

<br>

## 3단계

- 생성자 파라미터로 Set<Class<?>>를 전달하자.
- 전달 받은 클래스를 객체로 생성한다.
- 객체의 내부 필드의 타입에 맞는 객체(baen)를 찾아서 대입(assign)한다.
- DI에서 관리하는 객체(bean)를 찾아서 반환한다.

<br>

## 4단계

- stage3에서 구현한 기능을 기본적으로 제공한다.
- 생성자 파라미터로 패키지명을 받아서 클래스를 찾는 ClassPathScanner를 구현한다.
- @Service, @Repository가 존재하는 클래스만 객체로 생성한다.
- 객체에서 @Inject를 붙인 필드만 필터하고 타입에 맞는 객체(bean)를 찾아서 대입(assign)한다.
- DI에서 관리하는 객체(bean)를 찾아서 반환한다.

