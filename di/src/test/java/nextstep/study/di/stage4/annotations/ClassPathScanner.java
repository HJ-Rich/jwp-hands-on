package nextstep.study.di.stage4.annotations;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

public class ClassPathScanner {

    public static Set<Class<?>> getAllClassesInPackage(final String rootPackageName,
                                                       final Class<? extends Annotation>... annotations) {
        final Set<Class<?>> classes = new HashSet<>();
        final Reflections reflections = new Reflections(rootPackageName);

        for (Class<? extends Annotation> annotation : annotations) {
            classes.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        return classes;
    }

    private ClassPathScanner() {
    }
}
