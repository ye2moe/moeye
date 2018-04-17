package cn.ye2moe.moeye.rpc.client.proxy;

import cn.ye2moe.moeye.core.protocol.Header;
import cn.ye2moe.moeye.core.protocol.Message;
import cn.ye2moe.moeye.rpc.client.ConnectManage;
import cn.ye2moe.moeye.rpc.client.RPCFuture;
import cn.ye2moe.moeye.rpc.client.RpcClientHandler;
import cn.ye2moe.moeye.rpc.protocol.MethodConfig;
import cn.ye2moe.moeye.rpc.protocol.RpcRequest;
import cn.ye2moe.moeye.rpc.server.MethodProvider;
import cn.ye2moe.moeye.rpc.server.RpcService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by luxiaoxun on 2016-03-16.
 */
public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<T> clazz;

    private final static AtomicLong id = new AtomicLong(0);
    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    private static Map<String,MethodConfig> methodConfigMap = Maps.newConcurrentMap();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        MethodConfig config = getMethodConfig(method,clazz.getName());

        Header header = MethodConfig.RpcHeaderMaker
                        .newMaker()
                        .loadWithMethodConfig(config)
                        .withMessageId(id.incrementAndGet())
                        .make();

        //FutureTask task =
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        // Debug
        LOGGER.debug(method.getDeclaringClass().getName());
/*         LOGGER.debug(method.getName());
        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            LOGGER.debug(method.getParameterTypes()[i].getName());
        }
        if(args != null)
        for (int i = 0; i < args.length; ++i) {
            LOGGER.debug(args[i].toString());
        }
*/
        RpcClientHandler handler = ConnectManage.getInstance().chooseHandler();
        RPCFuture rpcFuture = handler.sendRequest(new Message<>(header,request));

        return  rpcFuture.get();
    }

    private MethodConfig getMethodConfig(Method method, String serviceName) {
        String key = serviceName + "." + method.getName();
        System.out.println("*******************" + key);
        if(methodConfigMap.containsKey(key)){
            return methodConfigMap.get(key);
        }
        MethodConfig config = null;
        MethodProvider methodProvider = method.getAnnotation(MethodProvider.class);
        if(methodProvider== null)
            config = MethodConfig.Builder.newBuilder().build();
        else {
            String mName = "".equals(methodProvider.methodName()) ? method.getName() : methodProvider.methodName();

            config = MethodConfig.Builder
                    .newBuilder()
                    .withCompressType(methodProvider.compressType())
                    .withSerializeType(methodProvider.serializeType())
                    .withTimeout(methodProvider.timeout())
                    .build();

            config.setServiceName(serviceName);

            config.setMethodName(mName);

        }

        methodConfigMap.put(key , config);
        return config;
    }

    @Override
    public RPCFuture call(String funcName, Object... args) {
        RpcClientHandler handler = ConnectManage.getInstance().chooseHandler();
        RpcRequest request = createRequest(this.clazz.getName(), funcName, args);

        RPCFuture rpcFuture = null;//handler.sendRequest(request);
        return rpcFuture;
    }

    private RpcRequest createRequest(String className, String methodName, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);

        Class[] parameterTypes = new Class[args.length];
        // Get the right class type
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
//        Method[] methods = clazz.getDeclaredMethods();
//        for (int i = 0; i < methods.length; ++i) {
//            // Bug: if there are 2 methods have the same name
//            if (methods[i].getName().equals(methodName)) {
//                parameterTypes = methods[i].getParameterTypes();
//                request.setParameterTypes(parameterTypes); // get parameter types
//                break;
//            }
//        }

//        LOGGER.debug(className);
//        LOGGER.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            LOGGER.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < args.length; ++i) {
            LOGGER.debug(args[i].toString());
        }

        return request;
    }

    private Class<?> getClassType(Object obj){
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }

}
