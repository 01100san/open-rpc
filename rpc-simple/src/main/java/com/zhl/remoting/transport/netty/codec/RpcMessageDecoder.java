package com.zhl.remoting.transport.netty.codec;

import com.zhl.compress.Compress;
import com.zhl.enums.CompressEnum;
import com.zhl.enums.SerializationTypeEnum;
import com.zhl.extensions.ExtensionLoader;
import com.zhl.remoting.constants.RpcConstants;
import com.zhl.remoting.dto.RpcMessage;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * <p>
 *  RpcMessage 解码器 （ByteBuf ---> RpcMessage）<br>
 *  使用 LengthFieldBasedFrameDecoder ，防止消息半包粘包
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
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     *
     * @param maxFrameLength    最大的帧长度，最大可接收的数据长度，超出就丢弃
     * @param lengthFieldOffset     长度字段的偏移量    5
     * @param lengthFieldLength    长度字段的长度       4       16 ≤ 消息的总长度 ≤ 2^31-1
     * @param lengthAdjustment    长度字段(十进制)为基准调整，还有几个字节是内容  -9      ==> 调整后的实际数据长度
     * @param initialBytesToStrip     从头剥离几个字节（从头跳过几个字节）        这里设为0，我们手动根据魔数调整
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if(decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
                if(frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                    try {
                        return decodeFrame(frame);
                    } catch (Exception e) {
                        log.error("Decoder error !");
                        throw e;
                    } finally {
                        frame.release();
                    }
                }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        // 检查魔数
        checkMagic(in);
        // 检查版本
        checkVersion(in);
        // 第一次读到的 int 是消息长度
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codec = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
            .messageType(messageType)
            .codec(codec)
            .requestId(requestId)
            .compress(compressType)
            .build();
        // 消息类型是心跳的请求数据
        if(messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        // 消息类型是心跳的响应数据
        if (messageType==RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        // 读取正文消息
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if(bodyLength > 0) {
            byte[] bodyBytes = new byte[bodyLength];
            in.readBytes(bodyBytes);
            // 解压缩
            String compressName = CompressEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bodyBytes = compress.decompress(bodyBytes);
            // 反序列化
            String serializeName = SerializationTypeEnum.getName(codec);
            log.info("codec name: [{}] ", serializeName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serializeName);
            // 消息类型是正常请求数据
            if (messageType==RpcConstants.REQUEST_TYPE) {
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                rpcMessage.setData(request);
            }else {
                // 消息类型是发送响应数据
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                rpcMessage.setData(response);
            }
        }
        return rpcMessage;
    }

    private void checkMagic(ByteBuf in) {
        byte[] tmp = new byte[RpcConstants.MAGIC_NUMBER.length];
        // 把 byte[] 赋给 tmp
        in.readBytes(tmp);
        for (int i = 0; i < tmp.length; i++) {
            if(tmp[i]!=RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if(version!=RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }
}
