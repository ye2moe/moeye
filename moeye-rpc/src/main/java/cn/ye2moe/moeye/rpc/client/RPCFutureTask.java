package cn.ye2moe.moeye.rpc.client;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class RPCFutureTask extends FutureTask<Object>{
    public RPCFutureTask(Callable<Object> callable) {
        super(callable);
    }

    public RPCFutureTask(Runnable runnable, Object result) {
        super(runnable, result);
    }

    @Override
    protected void done() {


    }
}
