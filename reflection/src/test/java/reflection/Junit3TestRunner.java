package reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        // TODO Junit3Test에서 test로 시작하는 메소드 실행
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // given
        final Class<Junit3Test> clazz = Junit3Test.class;
        final String expected = String.join(System.lineSeparator(), "Running Test1", "Running Test2", "");

        // when
        final Junit3Test junit3Test = clazz.getDeclaredConstructor().newInstance();
        final List<Method> methodsStartsWithTest = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("test"))
                .collect(Collectors.toList());

        for (Method method : methodsStartsWithTest) {
            method.invoke(junit3Test);
        }

        // then
        final String actual = out.toString();
        assertThat(actual).isEqualTo(expected);
    }
}
