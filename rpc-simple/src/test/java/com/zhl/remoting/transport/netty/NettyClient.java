package com.zhl.remoting.transport.netty;


import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.remoting.transport.netty.handlers.NettyClientHandler;
import com.zhl.remoting.transport.netty.handlers.NettyKryoDecoder;
import com.zhl.remoting.transport.netty.handlers.NettyKryoEncoder;
import com.zhl.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-12 9:44
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源  如 bootstrap, EventLoop
    static {
        KryoSerializer kryoSerializer = new KryoSerializer();
        bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap
            .group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            // 连接的超时时间 5000 ms，超过这个时间还是建立不上的话则代表连接失败
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                    /*
                     自定义序列化的编解码器
                     */
                    // 客户端向服务端发送的 RpcRequest 进行编码 => ByteBuf
                    ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                    // 对接收到的 RpcResponse 进行解码 ByteBuf =>  RpcResponse
                    ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                    ch.pipeline().addLast(new NettyClientHandler());
                }
            });
    }

    /**
     * 发送消息到服务端
     * @param rpcRequest    消息体
     * @return  服务端返回的数据
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("client connect {}", host + " : " + port);
            Channel channel = future.channel();
            if(channel!=null) {
                channel.writeAndFlush(rpcRequest).addListener(promise->{
                    if (promise.isSuccess()) {
                        logger.info("client send message: [{}]", rpcRequest.toString());
                    }else {
                        logger.error("Send failed: ", promise.cause());
                    }
                });
                // 阻塞等待，直到 Channel 关闭
                channel.closeFuture().sync();
                // 将服务端返回的数据 即 RpcResponse对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("occur exception when connect server:", e);
        }
        return null;
    }
}
