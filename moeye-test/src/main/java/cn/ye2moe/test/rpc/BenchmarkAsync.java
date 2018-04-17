package cn.ye2moe.test.rpc;

import cn.ye2moe.moeye.rpc.client.RPCFuture;
import cn.ye2moe.moeye.rpc.client.RpcClient;
import cn.ye2moe.moeye.rpc.client.proxy.IAsyncObjectProxy;
import cn.ye2moe.moeye.rpc.registry.ServiceDiscovery;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * rpc 异步 基准测试
 */
public class BenchmarkAsync {
    public static void main(String[] args) throws InterruptedException {
        final RpcClient rpcClient = new RpcClient(new ServiceDiscovery("www.ye2moe.cn:16888"));

        int threadNum = 10;
        final int requestNum = 1000;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        ExecutorService executorService = Executors.newCachedThreadPool();

        long startTime = System.currentTimeMillis();
        //benchmark for async call
        for (int i = 0; i < threadNum; ++i) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            IAsyncObjectProxy client = rpcClient.createAsync(Hello2Service.class);
                            RPCFuture helloFuture = client.call("hello");
                            String result = (String) helloFuture.get(3000, TimeUnit.MILLISECONDS);

                            System.out.println(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        countDownLatch.await();

        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("*******************Async call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum * threadNum)) / timeCost * 1000);
        System.out.println(msg);

        rpcClient.stop();

    }
}
