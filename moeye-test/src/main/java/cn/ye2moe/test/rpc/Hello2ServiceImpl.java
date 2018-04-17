package cn.ye2moe.test.rpc;

import cn.ye2moe.moeye.rpc.server.RpcService;

@RpcService(Hello2Service.class)
public class Hello2ServiceImpl implements Hello2Service {
    @Override
    public String hello(String name ,int index) {
        return "hello "+name+" :times "+index;
    }
}
