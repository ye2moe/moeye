package cn.ye2moe.moeye.core.annotation.server;

import cn.ye2moe.moeye.core.util.Constant;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WebServer {
    String value() default "8080";
}
