package com.zhl.remoting.transport.netty.handlers;

import com.zhl.serialize.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * <p>
 *  编码器
 *  对 RpcRequest 序列化 为 ByteBuf
 *      它负责处理"出站"消息，将消息格式转换为字节数组然后写入到字节数据的容器 ByteBuf 对象中。
 * @author zhl
 * @since 2024-07-12 14:03
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private KryoSerializer kryoSerializer;
    private Class<?> clazz;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(clazz.isInstance(msg)) {
            // 将消息序列化成 byte[]
            byte[] body = kryoSerializer.serialize(msg);
            /*
                在消息头传入消息体的长度，防止粘包，半包
             */
            // 将消息体的长度和内容写入 ByteBuf 中
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        }
    }
}
