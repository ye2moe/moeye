package cn.ye2moe.moeye.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequestParam {

    String value();
    boolean require() default true;
    String defaultValue() default "";
}
