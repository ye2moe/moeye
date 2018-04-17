package cn.ye2moe.moeye.rpc.protocol.compress;

import java.io.IOException;

/**
 * 压缩
 */
public interface Compress {

    byte[] compress(byte[] array) throws IOException;


    byte[] unCompress(byte[] array) throws IOException;
}
