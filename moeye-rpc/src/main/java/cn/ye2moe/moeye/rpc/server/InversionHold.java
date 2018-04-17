package cn.ye2moe.moeye.rpc.server;

import cn.ye2moe.moeye.core.ApplicationContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class InversionHold {
    private static InversionHold ourInstance;

    private Logger logger = Logger.getLogger(InversionHold.class);

    //uri handler mapping
    private Map<String, Object> serviceMap = new HashMap<String, Object>();

    static {
        ourInstance = new InversionHold();
    }

    public static void ioc(RpcServer rpcServer){
        ourInstance.logger.info("***** rpc ioc *****");
        ourInstance.serviceMap = ApplicationContext.getBeansWithAnnotation(RpcService.class);
        for(Object val: ourInstance.serviceMap.values()) {
            rpcServer.addService(
                    val.getClass().getAnnotation(RpcService.class).value().getName(),val);
        }
    }

}
