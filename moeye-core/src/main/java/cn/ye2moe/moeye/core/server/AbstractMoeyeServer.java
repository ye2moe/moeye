package cn.ye2moe.moeye.core.server;

public abstract class AbstractMoeyeServer implements MoeyeServer{

    public void start(Object info){
        initialization();
        run(info);
    }

    protected void initialization(){

    }

    protected abstract void run(Object info);


}
