package cn.ye2moe.moeye.web.core.server;

import cn.ye2moe.moeye.core.init.ApplicationContextAware;
import cn.ye2moe.moeye.core.server.AbstractMoeyeServer;
import cn.ye2moe.moeye.core.server.MoeyeServer;
import cn.ye2moe.moeye.web.core.server.ioc.InversionHold;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.log4j.Logger;

public class HttpServer extends AbstractMoeyeServer{
    private Logger logger = Logger.getLogger(HttpServer.class);

    protected void run(Object port){

        logger.info("****************** Server Start "+port+" **********************");

        int p = (Integer) port;
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup woker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(boss, woker)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("server-decoder", new HttpServerCodec());
                            ch.pipeline().addLast(new HttpServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(p).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            woker.shutdownGracefully();
        }
    }

    @Override
    protected void initialization() {
        InversionHold.setInversionHold();
    }

}