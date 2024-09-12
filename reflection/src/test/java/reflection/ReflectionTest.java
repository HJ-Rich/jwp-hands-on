package reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionTest.class);

    @Test
    void givenObject_whenGetsClassName_thenCorrect() {
        final Class<Question> clazz = Question.class;

        assertAll(
                () -> assertThat(clazz.getSimpleName()).isEqualTo("Question"),
                () -> assertThat(clazz.getName()).isEqualTo("reflection.Question"),
                () -> assertThat(clazz.getCanonicalName()).isEqualTo("reflection.Question")
        );
    }

    @Test
    void givenClassName_whenCreatesObject_thenCorrect() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("reflection.Question");

        assertAll(
                () -> assertThat(clazz.getSimpleName()).isEqualTo("Question"),
                () -> assertThat(clazz.getName()).isEqualTo("reflection.Question"),
                () -> assertThat(clazz.getCanonicalName()).isEqualTo("reflection.Question")
        );
    }

    @Test
    void givenObject_whenGetsFieldNamesAtRuntime_thenCorrect() {
        // given
        final Object student = new Student();

        // when
        final Class<?> clazz = student.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        final List<String> actualFieldNames = Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.toList());

        // then
        assertThat(actualFieldNames).contains("name", "age");
    }

    @Test
    void givenClass_whenGetsMethods_thenCorrect() {
        // given
        final Class<?> animalClass = Student.class;

        // when
        final Method[] methods = animalClass.getDeclaredMethods();
        final List<String> actualMethods = Arrays.stream(methods)
                .map(Method::getName)
                .collect(Collectors.toList());

        // then
        assertThat(actualMethods)
                .hasSize(3)
                .contains("getAge", "toString", "getName");
    }

    @Test
    void givenClass_whenGetsAllConstructors_thenCorrect() {
        // given
        final Class<?> questionClass = Question.class;

        // when
        final Constructor<?>[] constructors = questionClass.getConstructors();

        // then
        assertThat(constructors).hasSize(2);
    }

    @Test
    void givenClass_whenInstantiatesObjectsAtRuntime_thenCorrect() throws Exception {
        // given
        final Class<?> questionClass = Question.class;
        final Constructor<?> firstConstructor = questionClass.getConstructors()[0];
        final Constructor<?> secondConstructor = questionClass.getConstructors()[1];

        // when
        final Question firstQuestion = (Question) firstConstructor.newInstance("gugu", "제목1", "내용1");
        final Question secondQuestion = (Question) secondConstructor.newInstance(0, "gugu", "제목2", "내용2", null, 0);

        // then
        assertAll(
                () -> assertThat(firstQuestion.getWriter()).isEqualTo("gugu"),
                () -> assertThat(firstQuestion.getTitle()).isEqualTo("제목1"),
                () -> assertThat(firstQuestion.getContents()).isEqualTo("내용1"),
                () -> assertThat(secondQuestion.getWriter()).isEqualTo("gugu"),
                () -> assertThat(secondQuestion.getTitle()).isEqualTo("제목2"),
                () -> assertThat(secondQuestion.getContents()).isEqualTo("내용2")
        );
    }

    @Test
    void givenClass_whenGetsPublicFields_thenCorrect() {
        // given
        final Class<?> questionClass = Question.class;

        // when
        final Field[] fields = questionClass.getFields();

        // then
        assertThat(fields).hasSize(0);
    }

    @Test
    void givenClass_whenGetsDeclaredFields_thenCorrect() {
        // given
        final Class<?> questionClass = Question.class;

        // when
        final Field[] fields = questionClass.getDeclaredFields();

        // then
        assertAll(
                () -> assertThat(fields).hasSize(6),
                () -> assertThat(fields[0].getName()).isEqualTo("questionId")
        );
    }

    @Test
    void givenClass_whenGetsFieldsByName_thenCorrect() throws Exception {
        // given
        final Class<?> questionClass = Question.class;
        final String expectedFieldName = "questionId";

        // when
        final Field field = questionClass.getDeclaredField(expectedFieldName);

        // then
        assertThat(field.getName()).isEqualTo(expectedFieldName);
    }

    @Test
    void givenClassField_whenGetsType_thenCorrect() throws Exception {
        // given
        final Field field = Question.class.getDeclaredField("questionId");

        // when
        final Class<?> fieldClass = field.getType();

        // then
        assertThat(fieldClass.getSimpleName()).isEqualTo("long");
    }

    @Test
    void givenClassField_whenSetsAndGetsValue_thenCorrect() throws Exception {
        // given
        final Class<?> studentClass = Student.class;
        final Field field = studentClass.getDeclaredField("age");
        final Student student = (Student) studentClass.getDeclaredConstructor().newInstance();

        // TODO field에 접근 할 수 있도록 만든다.
        field.setAccessible(true);

        // when
        final int ageByFieldBeforeSet = field.getInt(student);
        final int ageByInstanceBeforeSet = student.getAge();

        field.set(student, 99);
        final int ageByFieldAfterSet = field.getInt(student);
        final int ageByInstanceAfterSet = student.getAge();

        // then
        assertAll(
                () -> assertThat(ageByFieldBeforeSet).isZero(),
                () -> assertThat(ageByInstanceBeforeSet).isZero(),
                () -> assertThat(ageByFieldAfterSet).isEqualTo(99),
                () -> assertThat(ageByInstanceAfterSet).isEqualTo(99)
        );
    }
}
