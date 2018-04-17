package cn.ye2moe.test.rpc;

import cn.ye2moe.moeye.rpc.client.RpcClient;
import cn.ye2moe.moeye.rpc.registry.ServiceDiscovery;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * rpc 同步 基准测试
 */
@State(Scope.Benchmark)
public class MyBenchmark {


    public static void main(String[] args) throws RunnerException {
        /*Options opt = new OptionsBuilder()
                .include(".*" + MyBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();*/
        new MyBenchmark().oldBenchmark();
    }

    RpcClient rpcClient;
    @Setup
    public void setup(){
        rpcClient = new RpcClient(new ServiceDiscovery("www.ye2moe.cn:16888"));

    }
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void oneBe(){
        final Hello2Service syncClient = rpcClient.create(Hello2Service.class);

        String result = syncClient.hello("ye2moe",1);

        System.out.println(result);
    }


    public void oldBenchmark() {
        final RpcClient rpcClient = new RpcClient(new ServiceDiscovery("www.ye2moe.cn:16888"));

        final int threadNum = 1;
        final int requestNum = 1000;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        ExecutorService executorService = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        //benchmark for sync call
        System.out.println("***************************************");
        final Hello2Service syncClient = rpcClient.create(Hello2Service.class);
        for (int i = 0; i < threadNum; ++i) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //long startTime = System.currentTimeMillis();

                    for (int i = 0; i < requestNum; i++) {
                        System.out.println("****************   " + i * threadNum + "   *****************");

                        String result = syncClient.hello("ye2moe",i*threadNum);
                        System.out.println(result);
                    }
                    countDownLatch.countDown();
                    //long timeCost = (System.currentTimeMillis() - startTime);
                    //String msg = String.format("***************Sync call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum)) / timeCost * 1000);
                    //System.out.println(msg);

                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("***************Sync call total-time-cost:%sms, req/s=%s" +
                        " , eachreq =%f", timeCost, ((double) (requestNum * threadNum)) / timeCost * 1000
                , (timeCost) / (float) (requestNum * threadNum) * 1000
        );
        System.out.println(msg);

        rpcClient.stop();
    }
}
