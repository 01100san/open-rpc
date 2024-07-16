package com.zhl.remoting.transport.netty;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.remoting.transport.netty.handlers.NettyKryoDecoder;
import com.zhl.remoting.transport.netty.handlers.NettyKryoEncoder;
import com.zhl.remoting.transport.netty.handlers.NettyServerHandler;
import com.zhl.serialize.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  开启一个服务端用于接收客户端的请求并处理
 * @author zhl
 * @since 2024-07-12 13:47
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 是否开启 TCP 底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 30 秒内没有收到客户端的读请求才会进行初始化
                        ch.pipeline().addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                        // 服务端对发送的请求 RpcResponse 编码 ==> ByteBuf
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                        // 对接收到的 RpcRequest 解码 ByteBuf ==> RpcRequest
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
            // 绑定端口，同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
