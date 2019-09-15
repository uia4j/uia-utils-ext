package uia.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnInfo {

    String name();

    boolean primaryKey() default false;

    String typeName() default "";

    boolean inView() default true;
}
