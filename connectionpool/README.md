<p align="center">
    <img src="./woowacourse.png" alt="우아한테크코스" width="250px">
</p>

# @MVC 구현하기

---

![Generic badge](https://img.shields.io/badge/Level4-ConnectionPool-green.svg)
![Generic badge](https://img.shields.io/badge/test-5_to_fix-red.svg)
![Generic badge](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)

> 우아한테크코스 웹 백엔드 4기, DB ConnectionPool 적용하기 저장소입니다.

<br><br>

## 미션 설명

---

### 학습 목표

- 애플리케이션이 필요로 할 때마다 매번 DB에 물리적 연결(Connection)을 만들면 시간과 리소스가 많이 사용된다.
- 서버 성능을 높이기 위해 Connection Pool을 사용하자.
- Connection Pool은 DB로부터 미리 Connection을 만들어 보관한다.

<br>

### 학습 순서

- JDBC 드라이버를 사용하여 데이터베이스에 연결하는 방법을 살펴본다.
- HikariCP가 어떤 것인지 다뤄보고 공식 문서를 읽어본다.
- MaximumPoolSize를 몇으로 설정하면 좋을지 고민하고 적용한다.

<br>

### 저장소

- jwp-hands-on
    - [connection pool](https://github.com/woowacourse/jwp-hands-on/tree/main/connectionpool)

<br>

### 0단계 - DataSource 다루기

- 자바에서 제공하는 JDBC 드라이버를 직접 다뤄본다.
- 데이터베이스에 어떻게 연결하는지, 그리고 왜 DataSource를 사용하는지 찾아보자.

<br>

### 1단계 - 커넥션 풀링

- 커넥션 풀링이 무엇인지 h2의 JdbcConnectionPool을 직접 다뤄본다.
    - JdbcConnectionPool 클래스가 복잡하지 않으니 직접 분석해봐도 좋다.
- 왜 스프링 부트에서 HikariCP를 사용하는지 찾아본다.
    - [spring boot docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data.sql.datasource.connection-pool)

- 그리고 HikariCP에 어떤 설정을 하면 좋을지 공식 문서를 읽어보자.
    - [HikariCP](https://github.com/brettwooldridge/HikariCP#rocket-initialization)
    - [About Pool Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)
    - [MySQL Configuration](https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration)

<br>

### 2단계 - HikariCP 설정하기

- 스프링 부트로 HikariCP를 설정하는 방법을 익힌다.
- 커넥션 풀이 의도한대로 동작하는지 테스트한다.

<br><br>

# 목차

- 0단계 - DataSource 다루기
    - 체스 미션에서 사용했었던 Driver Manager
    - JDBC 4.0 부터 적용된 Autoloading 기능
- 1단계 - 커넥션 풀링
    - H2의 JdbcConnectionPool 클래스 살펴보기
    - 스프링부트에서 HikariCP를 사용하는 이유
    - HikariCP에 어떤 설정을 하면 좋을까
- 2단계 - HikariCP 설정하기

## 0단계 - DataSource 다루기

- 자바에서 제공하는 JDBC 드라이버를 직접 다뤄본다.
- 데이터베이스에 어떻게 연결하는지, 그리고 왜 DataSource를 사용하는지 찾아보자.

---

### 체스 미션에서 사용했었던 Driver Manager

- 레벨1 체스 미션에서는 DriverManager 를 이용해 커넥션을 가져왔습니다.

- 이 방식의 단점은 커넥션 풀링을 지원하지 않기 때문에 매번 DB 접속이 필요할 때마다
  DB 서버와 새로 연결을 생성하는 비용이 반복적으로 발생하게 된다는 점입니다.

- 또한, DB 접속 정보를 Dao클래스의 상수로 가지고 있어서,
  DB 접속 정보가 바뀌면 애플리케이션 영역에까지 변경이 영향을 미치게 됩니다.

<br>

### JDBC 4.0 부터 적용된 Autoloading 기능

- 기존엔 Class.forName 메서드를 이용해 어떤 드라이버를 사용할지 지정해줘야 했습니다.
- 4.0 부터 제공되는 Autoloading 기능은 각 벤더사의 모듈 내 `META-INF/services/java.sql.Driver` 파일을 읽어와서 사용합니다.
- H2, MySQL, Oracle 에 대해서 확인해봤습니다.

<img width="700" alt="image" src="https://user-images.githubusercontent.com/62681566/193180869-055211d6-74cc-4386-9a5d-b7721f535e53.png">


<br><br><br>

## 1단계 - 커넥션 풀링

- 커넥션 풀링이 무엇인지 h2의 JdbcConnectionPool을 직접 다뤄본다.
    - JdbcConnectionPool 클래스가 복잡하지 않으니 직접 분석해봐도 좋다.
- 왜 스프링 부트에서 HikariCP를 사용하는지 찾아본다.
    - [spring boot docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data.sql.datasource.connection-pool)

- 그리고 HikariCP에 어떤 설정을 하면 좋을지 공식 문서를 읽어보자.
    - [HikariCP](https://github.com/brettwooldridge/HikariCP#rocket-initialization)
    - [About Pool Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)
    - [MySQL Configuration](https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration)

<br>

### H2의 JdbcConnectionPool 클래스 살펴보기

- 핵심 필드 2가지는 `ConnectionPoolDataSource`와 `Queue<PooledConnection>` 입니다.
    - `ConnectionPoolDataSource`는 실제 커넥션을 얻어올 수 있는 원천입니다.
    - `Queue<PooledConnection>`는 `ConnectionPoolDataSource`로부터 얻어온 커넥션들이 보관되는 장소입니다.
- getConnection() 메서드는
    - 현재 사용중인 커넥션 숫자와 최대 커넥션 설정을 비교해서 최대 설정 보다 현재 값이 낮다면 getConnectionNow()를 호출합니다.
    - getConnectionNow() 는 Queue에서 poll() 메서드를 호출하고, 결과가 null이면 dataSource로부터 커넥션을 얻어와 반환합니다.
- recycleConnection(PooledConnection) 메서드는
    - 사용이 완료된 커넥션을 반환받아 Queue에 담는 역할을 수행합니다.

<br>

### [스프링부트에서 HikariCP를 사용하는 이유](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data.sql.datasource.connection-pool)

- 성능과 동시성을 이유로 HikariCP가 가장 선호된다. 스프링은 사용 가능하다면 이것을 사용합니다.
- 그다음으로 Tomcat의 Datasource, Commons DBCP2, Oracle UCP 순으로 스프링은 우선하여 사용합니다.
- HikariCP의 성능 중 커넥션을 얻어오고 반환하는 사이클 및 쿼리를 준비해서 실행하고 종료하는 사이클에 대한 성능 테스트 결과를 첨부해보았습니다.

<img src="https://user-images.githubusercontent.com/62681566/193182586-93337202-47cb-46a0-a6e6-704b22ba9c0d.png" width="500">
https://github.com/brettwooldridge/HikariCP#checkered_flag-jmh-benchmarks

<br>

### HikariCP에 어떤 설정을 하면 좋을까

- Spring Bean으로 등록할 때 HikariCP 설정 방법

```java
HikariConfig config=new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/simpsons");
        config.setUsername("bart");
        config.setPassword("51mp50n");
        config.addDataSourceProperty("cachePrepStmts","true");
        config.addDataSourceProperty("prepStmtCacheSize","250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit","2048");

        HikariDataSource ds=new HikariDataSource(config);
```

<br>

- [HikariCP와 MySQL을 사용할 때 성능 최적화를 위해 HikariCP에서 권장하는 설정](https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration)
    - `prepStmtCacheSize` : 커넥션당 캐싱할 PreparedStatement 수. 기본값 25, 권장값 250-500
    - `prepStmtCacheSqlLimit ` : 드라이버가 캐싱할 Prepared SQL Statement 최대 길이. MySQL 기본값은 256. ORM과 함께 할 때 권장하는 값은 2048.
    - `cachePrepStmts` : 이 설정을 true로 해서 캐싱 설정을 활성화 해야 위 설정들이 적용됨. 기본값이 false임.
- 여기까지가 학습테스트에서 제공되었던 설정값들.
- 그 외에 추가 설정은 아래 참고

```
jdbcUrl=jdbc:mysql://localhost:3306/simpsons
username=test
password=test
dataSource.cachePrepStmts=true
dataSource.prepStmtCacheSize=250
dataSource.prepStmtCacheSqlLimit=2048
dataSource.useServerPrepStmts=true
dataSource.useLocalSessionState=true
dataSource.rewriteBatchedStatements=true
dataSource.cacheResultSetMetadata=true
dataSource.cacheServerConfiguration=true
dataSource.elideSetAutoCommits=true
dataSource.maintainTimeStats=false
```

<br><br><br>

# 2단계 - HikariCP 설정하기

- 스프링 부트로 HikariCP를 설정하는 방법을 익힌다.
- 커넥션 풀이 의도한대로 동작하는지 테스트한다.

<br><br>

## 테스트 환경

- SpringBootTest 적용 클래스에 Autowired 로 DataSource를 주입받은 환경이었습니다.
- Configuration 선언된 클래스에서 Bean 애너테이션으로 HikariDataSource를 반환하고 있습니다.
- 풀 이름, 연결 설정 및 추가 프로퍼티 설정이 주입되었고, 이것이 테스트 클래스에서도 인식되어 정상동작하는지 확인했습니다.

<br>

## 테스트 내용

- Autowired된 DataSource로부터 커넥션을 얻어와 0.5초간 점유하다가 작업을 마치는 Runnable을 선언합니다.
- 반복문을 통해 앞서 선언한 Runnable을 생성자 매개변수로 전달하며 20개의 Thread를 생성합니다.
- 20개의 스레드를 동시에 실행하고, join문을 통해 모두 완료될 때까지 기다립니다.
- 완료 이후에 DataSource의 커넥션 갯수와 풀 이름을 확인합니다.

<br>

## 확인한 내용

- yml 설정보다 bean 설정이 우선합니다
- HikariCP의 경우 기본 풀 이름이 HikariPool-1 입니다
- 20개의 요청이 동시에 오더라도 최대 풀 사이즈 이상으로 커넥션을 생성하지 않습니다.
- 최대 풀 크기를 5로 설정해둔 상태에서 20명의 요청이 동시에 왔고 한 요청 처리에 0.5초가 소요됩니다.
    - 서버 성능은 10 TPS가 되고, 따라서 20개명의 요청을 모두 처리하는데 2초가 소요됩니다.

<br><br>
