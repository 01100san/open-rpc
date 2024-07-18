package com.zhl.remoting.transport.netty.handlers;

import com.zhl.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>
 *  解码器
 *  解析 RpcRequest 信息
 *      它负责处理"入站"消息，它会从 ByteBuf 中读取到业务对象对应的字节序列，然后再将字节序列转换为我们的业务对象。
 * @author zhl
 * @since 2024-07-12 14:03
 */
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(NettyKryoDecoder.class);
    private KryoSerializer kryoSerializer;
    private Class<?> clazz;

    /**
     * Netty 传输的消息长度即 对象序列化后的消息长度 xxxEncoder，存储在 ByteBuf 的消息头  ===>  int
     */
    private static final int BODY_LENGTH = 4;

    /**
     *  该方法将一直被调用，直到从该方法返回时输入的ByteBuf没有任何内容可读取，或者直到从输入的ByteBuf中没有读取任何内容
     *
     * @param ctx    解码器关联的 ChannelHandlerContext 对象
     * @param in      "入站"数据，也就是客户端输出的 ByteBuf 对象
     * @param out    解码之后的数据对象添加到  List 集合中
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 接收到的 ByteBuf 必须大于 4 字节，在编码器中 设置的消息长度是 4个字节
        if(in.readableBytes() >= BODY_LENGTH) {
            // 标记当前 readIndex 的位置, 以便后面 读取消息不完整时 重置 readIndex
            in.markReaderIndex();
            // 读取 ByteBuf 的消息长度
            int dataLength = in.readInt();
            if(dataLength < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            // 如果可读字节数小于消息长度，说明是不完整的消息，重置 readIndex
            if(in.readableBytes() < dataLength) {
                in.markReaderIndex();
                return;
            }
            // 开始反序列化 解码
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            // 将 byte[] 转成目标对象
            Object obj = kryoSerializer.deserialize(body, clazz);
            out.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
