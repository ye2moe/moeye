package cn.ye2moe.moeye.rpc.server;

import java.lang.annotation.*;

/**
 * RPC annotation for RPC service
 *
 * @author huangyong
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcService {
    Class<?> value();
}
