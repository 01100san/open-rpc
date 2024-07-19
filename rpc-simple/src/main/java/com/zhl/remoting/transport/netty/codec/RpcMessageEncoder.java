package com.zhl.remoting.transport.netty.codec;

import com.zhl.compress.Compress;
import com.zhl.enums.CompressEnum;
import com.zhl.enums.SerializationTypeEnum;
import com.zhl.extensions.ExtensionLoader;
import com.zhl.remoting.constants.RpcConstants;
import com.zhl.remoting.dto.RpcMessage;
import com.zhl.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *  RpcMessage 编码器 （RpcMessage ---> ByteBuf）
 * <p>
 * custom protocol decoder
 * <pre>
 *    | 0   1     2     3     4    |     5     |    6     7     8     9  |        10            | 11       |    12         |    13  14   15 16|
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                                                                                   |
 *   |                                         body                                                                                                                 |
 *   |                                                                                                                                                                   |
 *   |                                        ... ...                                                                                                                    |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B codec（序列化类型）      1B compress（压缩类型）     4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * @author zhl
 * @since 2024-07-17 15:32
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    // 记录 requestId
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // 写指针向后移4个字节，跳过消息的总长度
            out.writerIndex(out.writerIndex()+4);
            byte messageType = msg.getMessageType();
            out.writeByte(messageType);
            out.writeByte(msg.getCodec());
            out.writeByte(CompressEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            // 如果消息类型不是心跳类型，则消息总长度=消息头长度（16）+消息体长度（bodyBytes.length）
            int fullLength = RpcConstants.HEAD_LENGTH;
            if (messageType!=RpcConstants.HEARTBEAT_REQUEST_TYPE &&
                messageType!=RpcConstants.HEARTBEAT_RESPONSE_TYPE ) {
                // 开始序列化
                String codecName = SerializationTypeEnum.getName(msg.getCodec());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
                log.info("codec name: [{}] ", codecName);
                bodyBytes = serializer.serialize(msg.getData());
                // 开始压缩
                String compressName = CompressEnum.getName(msg.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).
                    getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                // 更新总长度
                fullLength += bodyBytes.length;
            }
            // 写入消息体
            if(bodyBytes!=null) {
                out.writeBytes(bodyBytes);
            }
            // 获取当前的写指针
            int writerIndex = out.writerIndex();
//            log.info("currentWriteIndex={}", writerIndex);
            // 重新回到跳过前的位置
            out.writerIndex(writerIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
//            log.info("lastWriterIndex={}", writerIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            // 写入总消息长度
            out.writeInt(fullLength);
            // 重新回到当前的写指针位置
            out.writerIndex(writerIndex);
//            log.info("currentWriteIndex={}", writerIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}
