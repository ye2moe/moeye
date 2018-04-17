package cn.ye2moe.moeye.rpc.client.proxy;

import cn.ye2moe.moeye.rpc.client.RPCFuture;

/**
 * Created by luxiaoxun on 2016/3/16.
 */
public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}