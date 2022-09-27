package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContext {

    private final Reflections reflections;
    private final Set<Object> beans;

    public DIContext(final Set<Class<?>> classes) {
        this.reflections = new Reflections("nextstep.study.di.stage3.context");
        this.beans = new HashSet<>();

        beans.addAll(
                classes.stream()
                        .map(this::newInstance)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

    private Object newInstance(Class<?> clazz) {
        if (clazz.isInterface()) {
            clazz = findSubtypeClassInfo(clazz);
        }

        final Constructor<?> constructor = clazz.getConstructors()[0];
        final Object[] parameters = findParameters(constructor);

        return newInstance(constructor, parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(aClass::isInstance)
                .findAny()
                .orElseGet(() -> {
                    final Object instance = newInstance(aClass);
                    beans.add(instance);
                    return instance;
                });
    }

    private Class<?> findSubtypeClassInfo(Class<?> clazz) {
        return reflections.getSubTypesOf(clazz)
                .stream()
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    private Object[] findParameters(final Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();

        return Arrays.stream(parameterTypes)
                .map(this::getBean)
                .toArray();
    }

    private Object newInstance(final Constructor<?> constructor, final Object[] parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
