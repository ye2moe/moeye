package cn.ye2moe.moeye.hot.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableHotCode {
    /**
     * 热代码包路径
     * @return
     */
    String value();
}
