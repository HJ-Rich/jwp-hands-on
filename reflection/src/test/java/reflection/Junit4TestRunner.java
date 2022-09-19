package reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        // TODO Junit4Test에서 @MyTest 애노테이션이 있는 메소드 실행
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // given
        final Class<Junit4Test> clazz = Junit4Test.class;
        final List<String> expected = List.of("Running Test1", "Running Test2");

        // when
        final Junit4Test junit4Test = clazz.getDeclaredConstructor().newInstance();
        final List<Method> methodsAnnotatedByMyTest = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MyTest.class))
                .collect(Collectors.toList());

        for (Method method : methodsAnnotatedByMyTest) {
            method.invoke(junit4Test);
        }

        // then
        final String actual = out.toString();
        assertThat(actual).contains(expected);
    }
}
