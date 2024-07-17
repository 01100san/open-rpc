package com.zhl.remoting.transport.netty.client;

import com.zhl.enums.RpcErrorMessageEnum;
import com.zhl.enums.ServiceRegistryEnum;
import com.zhl.exception.RpcException;
import com.zhl.extensions.ExtensionLoader;
import com.zhl.factory.SingletonFactory;
import com.zhl.registry.ServiceDiscovery;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
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
import org.checkerframework.common.returnsreceiver.qual.This;

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

    private final ServiceDiscovery serviceDiscovery;
    private final Bootstrap bootstrap;
    private final NioEventLoopGroup nioEventLoopGroup;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;


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
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(ServiceRegistryEnum.ZK.getName());
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }



    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel chanel = getChanel(inetSocketAddress);
        if (chanel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            // 封装自定义Rpc消息，包括协议等
            chanel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future -> {
                if (future.isSuccess()) {
                    log.info("");
                }else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.info("");
                }
            });
        }else {
            throw new RpcException(RpcErrorMessageEnum.CLIENT_CONNECT_SERVER_FAILURE);
        }

        return resultFuture;
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

    /**
     * 获取和服务建立连接后的 Channel
     * @param inetSocketAddress 建立连接的服务
     * @return
     */
    public Channel getChanel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if(channel==null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        nioEventLoopGroup.shutdownGracefully();
    }
}
