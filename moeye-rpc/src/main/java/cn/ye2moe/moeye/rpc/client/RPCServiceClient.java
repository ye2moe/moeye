package cn.ye2moe.moeye.rpc.client;

import java.lang.annotation.*;

/**
 * RPC annotation for RPC service
 *
 * @author huangyong
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RPCServiceClient {

}
