package com.zhl.remoting.transport.netty.handler;

import com.zhl.enums.CompressEnum;
import com.zhl.enums.SerializationTypeEnum;
import com.zhl.factory.SingletonFactory;
import com.zhl.remoting.constants.RpcConstants;
import com.zhl.remoting.dto.RpcMessage;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.remoting.transport.netty.client.ChannelProvider;
import com.zhl.remoting.transport.netty.client.UnprocessedRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

/**
 * <p>
 * 自定义 ChannelHandler 处理服务端发送的消息
 *
 * @author zhl
 * @since 2024-07-18 11:24
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyRpcClientHandler() {
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * 读取来自服务端的数据
     * 消息类型是消息的响应，或服务端的心跳响应
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                byte messageType = rpcMessage.getMessageType();
                if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> data = (RpcResponse<Object>) rpcMessage.getData();
                    // 异步操作得到的数据
                    unprocessedRequests.compute(data);
                }
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", rpcMessage.getData());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 客户端触发写事件，向服务端发送心跳数据
        if (evt instanceof IdleStateEvent) {
            IdleState idleState = ((IdleStateEvent) evt).state();
            if (idleState == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                // 拿到 NettyRpcClient 中的 channel
                Channel channel = channelProvider.get((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = RpcMessage.builder()
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .codec(SerializationTypeEnum.KRYO.getCode())
                        .compress(CompressEnum.GZIP.getCode())
                        .data(RpcConstants.PING)
                        .build();
                // 客户端向服务端发送写事件 (ping)，监听关闭事件 ，当写事件失败时，关闭 channel 通道
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception：", cause);
        cause.printStackTrace();;
        ctx.close();
    }
}
