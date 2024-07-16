package com.zhl.remoting.transport.netty.client;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.transport.RpcRequestTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoop;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 15:09
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private Bootstrap bootstrap;

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
}
