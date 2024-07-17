package com.zhl.remoting.transport.netty.client;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.transport.RpcRequestTransport;
import com.zhl.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 15:09
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private Bootstrap bootstrap;
    private NioEventLoopGroup nioEventLoopGroup;


    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap
            .group(nioEventLoopGroup)
            .channel(NioSocketChannel.class)
            // 设置超时时间
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        // 5s 内没有写事件触发空闲事件
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));

                }
            });

    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        // 创建 CompletableFuture 对象，用于异步操作结果的存储
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        // 为连接操作(异步)添加一个监听器，该监听器会在连接操作完成后被调用
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                // 将 connect 异步操作的结果存储到 completableFuture 中
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException();
            }
        });
        // 阻塞等待异步操作完成返回结果
        return completableFuture.get();
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }

    public void close() {
        nioEventLoopGroup.shutdownGracefully();
    }
}
