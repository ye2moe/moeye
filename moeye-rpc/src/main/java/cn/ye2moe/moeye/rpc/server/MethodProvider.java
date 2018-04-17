package cn.ye2moe.moeye.rpc.server;


import cn.ye2moe.moeye.rpc.protocol.ProtocolConstants;
import cn.ye2moe.moeye.rpc.protocol.compress.CompressType;
import cn.ye2moe.moeye.rpc.protocol.serialize.SerializeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dempe on 2016/12/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodProvider {

    String methodName() default "";

    SerializeType serializeType() default SerializeType.Kryo;

    CompressType compressType() default CompressType.None;

    int timeout() default ProtocolConstants.DEFAULT_TIMEOUT; // 客户端超时时间

}

