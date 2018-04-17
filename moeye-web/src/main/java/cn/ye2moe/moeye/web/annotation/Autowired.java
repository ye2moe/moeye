package cn.ye2moe.moeye.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Autowired {
    String value() default "";
}
