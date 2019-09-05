package uia.utils.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInfo {

    String name();

    int inherit() default 0;
}
