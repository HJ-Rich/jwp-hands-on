package reflection;

import static org.assertj.core.api.Assertions.assertThat;

import annotation.Controller;
import annotation.Repository;
import annotation.Service;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionsTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsTest.class);

    @Test
    void showAnnotationClass() throws Exception {
        // given
        Reflections reflections = new Reflections("examples");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // TODO 클래스 레벨에 @Controller, @Service, @Repository 애노테이션이 설정되어 모든 클래스 찾아 로그로 출력한다.
        // when
        final List<String> controllers = joinClassNamesAnnotatedWith(reflections, Controller.class);
        final List<String> services = joinClassNamesAnnotatedWith(reflections, Service.class);
        final List<String> repositories = joinClassNamesAnnotatedWith(reflections, Repository.class);

        log.info("controllers : {}", controllers);
        log.info("services : {}", services);
        log.info("repositories : {}", repositories);

        // then
        final String actual = out.toString();
        assertThat(actual).contains("examples.QnaController", "examples.MyQnaService", "examples.JdbcUserRepository",
                "examples.JdbcQuestionRepository");
    }

    private static List<String> joinClassNamesAnnotatedWith(final Reflections reflections, Class<?> clazz) {
        return reflections.getTypesAnnotatedWith((Class<? extends Annotation>) clazz)
                .stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }
}
