package cn.ye2moe.moeye.rpc.client;

import cn.ye2moe.moeye.rpc.protocol.codec.RpcDecoder;
import cn.ye2moe.moeye.rpc.protocol.codec.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by luxiaoxun on 2016-03-16.
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder());
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder());
        cp.addLast(new RpcClientHandler());
    }
}
