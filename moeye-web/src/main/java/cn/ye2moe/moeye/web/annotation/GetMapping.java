package cn.ye2moe.moeye.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GetMapping {
    String value() default "";
    String method() default "GET";
}
