package cn.ye2moe.moeye.core.annotation.server;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RPCServer {
}
