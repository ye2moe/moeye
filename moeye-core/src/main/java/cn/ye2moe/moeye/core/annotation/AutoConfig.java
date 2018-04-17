package cn.ye2moe.moeye.core.annotation;


import java.lang.annotation.*;

/**
 * 开启自动配置,扫描resources文件夹
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AutoConfig {
}
