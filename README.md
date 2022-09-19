# JWP Hands-On

## 만들면서 배우는 스프링 실습 코드

### 학습 순서

- cache
- thread
- servlet
- reflection
- di

## Reflection

- [x] Junit3TestRunner 클래스의 모든 테스트를 통과시킨다.
    - Reflection으로 Junit3Test 클래스 정보를 가져온다.
    - 생성자를 이용해 인스턴스를 만든다.
    - 메서드 정보를 가져온 뒤, test로 시작하는 메서드들을 filtering한다.
    - 필터된 메서드들을 실행할 때 매개변수로 생성해두었던 인스턴스를 전달한다.
    - `method.getName().startsWith("test");`
- [x] Junit4TestRunner 클래스의 모든 테스트를 통과시킨다.
    - Junit3TestRunner와 거의 흡사하나 필터링 기준만 다르다
    - MyTest 애너테이션의 존재 여부로 필터링한다.
    - `method.isAnnotationPresent(MyTest.class);`
- [ ] ReflectionTest 클래스의 모든 테스트를 통과시킨다.
- [ ] ReflectionsTest 클래스의 모든 테스트를 통과시킨다.

<br>

### Console 테스트

```java
final ByteArrayOutputStream out=new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

// run test logic..

final String actual=out.toString();
        assertThat(actual).isEqualTo(expected);
```

<br><br>
