package cn.ye2moe.moeye.rpc.server;


import cn.ye2moe.moeye.core.protocol.Header;
import cn.ye2moe.moeye.core.protocol.Message;
import cn.ye2moe.moeye.rpc.protocol.MessageType;
import cn.ye2moe.moeye.rpc.protocol.RpcRequest;
import cn.ye2moe.moeye.rpc.protocol.RpcResponse;
import cn.ye2moe.moeye.rpc.support.StandardThreadExecutor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * RPC Handler（RPC request processor）
 *
 * @author luxiaoxun
 */
public class RpcHandler extends SimpleChannelInboundHandler<Message<RpcRequest>> {

    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    private static Executor executor = new StandardThreadExecutor();

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Message<RpcRequest> message) throws Exception {
        logger.debug("Receive request " + message.getHeader().getMessageID());
        final RpcRequest request = message.getContent();
        if (handlerMap.get(request.getClassName()) == null) {
            logger.warn("no handle method :{}", request.getClassName());
            return;
        }
        executor.execute(new InvokerRunnable(message, handlerMap, ctx));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }

    class InvokerRunnable implements Runnable {

        Message<RpcRequest> message;
        Map<String, Object> handlerMap;
        ChannelHandlerContext ctx;

        public InvokerRunnable(Message<RpcRequest> message, Map<String, Object> handlerMap, ChannelHandlerContext ctx) {
            this.message = message;
            this.handlerMap = handlerMap;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                Object result = handle(message.getContent());

                RpcResponse response = new RpcResponse();

                response.setResult(result);

                byte extend = (byte)(message.getHeader().getExtend() | MessageType.RESPONSE_MESSAGE_TYPE);

                message.getHeader().setExtend(extend);
                ctx.writeAndFlush(new Message(message.getHeader(), response))
                        .addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        logger.debug("Send response for request " + message.getHeader().getMessageID());
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        private Object handle(RpcRequest request) throws Throwable {
            String className = request.getClassName();
            Object serviceBean = handlerMap.get(className);

            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            /*
            logger.debug(serviceClass.getName());
            logger.debug(methodName);
            for (int i = 0; i < parameterTypes.length; ++i) {
                logger.debug(parameterTypes[i].getName());
            }
            if(parameters!=null)
                for (int i = 0; i < parameters.length; ++i) {
                    logger.debug(parameters[i].toString());
            }
            */
            // Cglib reflect
            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            return serviceFastMethod.invoke(serviceBean, parameters);
        }
    }
}
