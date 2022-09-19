# JWP Hands-On

## 만들면서 배우는 스프링 실습 코드

### 학습 순서

- cache
- thread
- servlet
- reflection
- di

---

# Servlet

## 1단계 서블릿 학습 테스트

- [x] `SharedCounterServlet`, `LocalCounterServlet` 클래스를 열어보고 어떤 차이점이 있는지 확인한다.
    - service메서드에서 로깅하는 counter가 여러 스레드에 공유되는지 여부가 다르다.
    - SharedCounterServlet은 요청이 올 때마다 여러 스레드에서 하나의 공유된 counter에 접근한다.
        - 따라서 요청을 처리한 횟수가 늘어날수록 로깅되는 카운팅이 늘어난다.
    - 반면, LocalCounterServlet은 요청이 올 때마다 service 메서드 내부에서 counter를 0으로 초기화한 뒤, 1 증가시키고 로깅한다.
        - 따라서 요청을 처리한 횟수가 늘어나도 매번 1만 로깅된다.
    - 멀티스레드 환경에서 변화하는 상태값은 이러한 점을 인지하고 주의해서 사용해야 한다.

<br>

- [x] `ServletTest`를 통과시킨다.
    - expected를 적절한 값으로 수정함으로써 두 테스트를 성공시켰다.
    - `testSharedCounter` 는 3, `testLocalCounter` 는 1로 수정했다.
    - 서블릿은 서블릿 컨테이너에 하나만 등록되기 때문에, 같은 `urlPattern`에 대한 요청을 같은 서블릿이 처리하게 된다.
    - 따라서 서블릿 내부에 필드로 상태값을 갖게 되면 이것이 모든 요청에 공유되기 때문이다.
    - SharedCounterServlet의 필드인 counter는 계속 증가하고, LocalCounterServlet의 필드가 아닌 counter는 매번 초기화된다.

<br>

- [x] init, service, destroy 메서드가 각각 언제 실행되는지 콘솔 로그에서 확인한다.
    - `init` -> `doFilter` -> `service` -> `doFilter` -> `service` -> `...` -> `destroy`
    - init은 서블릿이 서블릿 컨테이너에 등록되는 최초 시점에 한 번 호출된다.
    - doFilter는 서블릿의 service가 호출되기 전 매번 호출된다.
    - service는 서블릿이 urlPattern이 매핑되어 요청을 처리할 때마다 호출된다.
    - destroy는 기동이 종료될 때 마지막에 한 번 호출된다.

<br>

- [x] 왜 이런 결과가 나왔는지 다른 크루와 이야기해보자.
    - MVC 미션의 리뷰어, 리뷰이와 위에 작성된 내용을 토대로 이야기를 나누었습니다.
    - 멀티스레드에서 공유되는 상태값이 있을 경우, 의도하지 않은 상황이 발생할 수 있으므로, 상태값이 없도록 구성하는 것의 필요성을 앞서 로또 미션에서 학습한 바 있습니다.
    - 최초엔 1에서 45까지의 숫자를 담은 리스트를 상태값으로 갖고, 이를 요청 시마다 shuffle하여 앞에서 6개의 숫자를 꺼내는 식으로 구현하였습니다.
    - Stream API의 parallel 기능을 이용해 병렬적으로 테스트를 시도하자, 중복된 숫자가 가져와지는 이슈가 발생했습니다.
    - 하나의 스레드에서 shuffle 수행 후 앞에서 6개를 꺼내는 동안, 다른 스레드가 다시 shuffle을 해버려서 앞서 꺼내던 스레드에서 중복된 숫자를 꺼내개 되는 이슈였습니다.
    - 1에서 45까지의 숫자를 방어적 복사를 한 뒤에, 이를 shuffle하여 꺼내는 식으로 상태값을 직접 사용하지 않도록 개선한 바 있습니다.

<br>

- [x] 직접 톰캣 서버를 띄워보고 싶다면 ServletApplication 클래스의 main 메서드를 실행한다.
- [x] 웹 브라우저에서 localhost:8080/shared-counter 경로에 접근 가능한지 확인한다.
    - http://localhost:8080/shared-counter 경로 접근 시도
        - 최초 접근 시 init 호출, 그 이후 service 호출
        - 재차 접근 시 init 호출 없이 즉시 service 호출
        - 호출 횟수가 늘어날수록 응답되는 counter가 점점 커짐
    - http://localhost:8080/local-counter 경로 접근 시도
        - 최초 접근 시 init 호출, 그 이후 service 호출
        - 재차 접근 시 init 호출 없이 즉시 service 호출
        - 호출 횟수가 늘어나도 응답되는 counter는 1로 동일

<br>  

## 2단계 - 필터 학습 테스트

- [x] FilterTest를 통과시킨다.
    - CharacterEncodingFilter에서 ServletResponse의 CharacterEncoding을 UTF-8로 설정함으로써 테스트 통과
- [x] doFilter 메서드는 어느 시점에 실행될까? 콘솔 로그에서 확인한다.
    - service 메서드가 호출되기 전에 실행된다.
    - `9월 20, 2022 2:52:37 오전 org.apache.catalina.core.ApplicationContext log    정보: doFilter() 호출`
    - `9월 20, 2022 2:52:37 오전 org.apache.catalina.core.ApplicationContext log    정보: service() 호출`
- [x] 왜 인코딩을 따로 설정해줘야 할까?
    - https://docs.oracle.com/javaee/7/api/javax/servlet/ServletResponse.html
    - SMTP, HTML 등의 응답의 기본 인코딩은 `ISO-8859-1` 이다.
    - 이 인코딩은 한글을 지원하지 않는다.

<br>
