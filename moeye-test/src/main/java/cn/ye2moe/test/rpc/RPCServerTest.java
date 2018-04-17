package cn.ye2moe.test.rpc;


import cn.ye2moe.moeye.core.MoeyeApplication;
import cn.ye2moe.moeye.core.annotation.AutoConfig;
import cn.ye2moe.moeye.core.annotation.server.RPCServer;
import cn.ye2moe.moeye.rpc.client.RPCServiceClient;
import cn.ye2moe.moeye.rpc.client.RpcClient;
import cn.ye2moe.test.web.WebTest;

@RPCServer
@AutoConfig
public class RPCServerTest {

    public static void main(String []args) {
        MoeyeApplication.run(RPCServerTest.class, args);
    }
}
