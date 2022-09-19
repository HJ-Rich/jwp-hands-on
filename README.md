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
- [x] ReflectionTest 클래스의 모든 테스트를 통과시킨다.
    - `getConstructor()` 메서드는 public 생성자만 배열로 반환한다
    - `getDeclaredConstructor()` 메서드는 private 생성자까지 포함해 배열로 반환한다
    - Field 에 instance를 전달하며 해당 instance의 필드값을 조회할 수도 있다.
    - 단, private 필드일 경우, setAccessible(true) 선언이 선행되어야 한다.
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
