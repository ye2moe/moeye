package cn.ye2moe.moeye.rpc.client;

import cn.ye2moe.moeye.core.protocol.Message;
import cn.ye2moe.moeye.rpc.protocol.RpcRequest;
import cn.ye2moe.moeye.rpc.protocol.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by luxiaoxun on 2016-03-14.
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Message<RpcResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private ConcurrentHashMap<Long, RPCFuture> pendingRPC = new ConcurrentHashMap();

    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message<RpcResponse> response) throws Exception {
        long requestId = response.getHeader().getMessageID();
        RPCFuture rpcFuture = pendingRPC.get(requestId);
        if (rpcFuture != null) {
            pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RPCFuture sendRequest(Message<RpcRequest> request) {
        logger.debug("send    ******" + request.getHeader());
        final CountDownLatch latch = new CountDownLatch(1);
        //Callable<String> task = () -> searcher.search(target);
        //FutureTask rpcFuture = new FutureTask()
        RPCFuture rpcFuture = new RPCFuture(request);
        pendingRPC.put(request.getHeader().getMessageID(), rpcFuture);
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        return rpcFuture;
    }


}
