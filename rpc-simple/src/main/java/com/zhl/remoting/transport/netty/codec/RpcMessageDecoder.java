package com.zhl.remoting.transport.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

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

    /**
     *
     * @param maxFrameLength    最大的帧长度，最大可接收的数据长度，超出就丢弃
     * @param lengthFieldOffset     长度字段的偏移量
     * @param lengthFieldLength    长度字段的长度
     * @param lengthAdjustment    长度字段(十进制)为基准调整，还有几个字节是内容
     * @param initialBytesToStrip     从头剥离几个字节（从头跳过几个字节）
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


}
