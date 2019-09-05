package uia.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

public class Reflections {

    private Reflections() {
    }

    public static List<Class<?>> findClasses(String packageName, ClassLoader loader) throws IOException {
        ClassLoader classLoader = loader == null
                ? Thread.currentThread().getContextClassLoader()
                : loader;
        assert classLoader != null;

        ClassPath cp = ClassPath.from(classLoader);
        ImmutableSet<ClassPath.ClassInfo> cis = cp.getTopLevelClasses(packageName);
        ArrayList<Class<?>> result = new ArrayList<>();
        cis.forEach(ci -> {
            try {
                result.add(Class.forName(ci.getName()));
            }
            catch (Exception ex) {

            }
        });
        return result;
    }

    public static List<Class<?>> findClasses(String packageName, Class<? extends Annotation> annotationClass, ClassLoader loader) throws IOException {
        ClassLoader classLoader = loader == null
                ? Thread.currentThread().getContextClassLoader()
                : loader;
        assert classLoader != null;

        ClassPath cp = ClassPath.from(classLoader);
        ImmutableSet<ClassPath.ClassInfo> cis = cp.getTopLevelClasses(packageName);
        ArrayList<Class<?>> result = new ArrayList<>();
        cis.forEach(ci -> {
            try {
                Class<?> clz = Class.forName(ci.getName());
                if (clz.isAnnotationPresent(annotationClass)) {
                    result.add(clz);
                }
            }
            catch (Exception ex) {

            }
        });
        return result;
    }
}
