package cn.ye2moe.moeye.rpc.protocol.serialize;

import java.io.IOException;

/**
 *  序列化
 */
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException, ClassNotFoundException;
}

