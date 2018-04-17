package cn.ye2moe.test.rpc;

import cn.ye2moe.moeye.core.annotation.AutoConfig;
import cn.ye2moe.moeye.rpc.client.RpcClient;
import cn.ye2moe.moeye.rpc.registry.ServiceDiscovery;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoConfig
public class RpcClientTest {

    RpcClient rpcClient;

    public static void main(String []args) {
        //MoeyeApplication.run(RpcClientTest.class, args);
        /*
        RpcClient rpcClient = new RpcClient(new ServiceDiscovery("www.ye2moe.cn:16888"));

        Hello2Service helloService = rpcClient.create(Hello2Service.class);

        for(int i=0 ;i<10 ;i++)
        System.out.println("****************"+ i +"___ "+ helloService.hello());
        */
        benchmarkTest();
    }

    public static void benchmarkTest(){
        RpcClient rpcClient = new RpcClient(new ServiceDiscovery("www.ye2moe.cn:16888"));

        final Hello2Service helloService = rpcClient.create(Hello2Service.class);

        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i =0;i<20;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for(int j = 0;j< 1000000000; j++){
                        String res = helloService.hello("abc",j);
                        if(j%10000 == 0){
                            System.out.println(res);
                        }
                    }
                }
            });
        }

    }


}
