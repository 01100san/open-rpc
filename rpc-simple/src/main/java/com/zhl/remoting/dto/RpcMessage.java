package com.zhl.remoting.dto;

import lombok.*;

/**
 * <p>
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
 * @since 2024-07-17 15:26
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    /**
     * 消息类型 (1B)
     */
    private byte messageType;
    /**
     * 压缩类型 (1B)
     */
    private byte compress;
    /**
     * 序列化类型 (1B)
     */
    private byte codec;
    /**
     * 请求 id (4B)
     */
    private int requestId;
    /**
     * 请求数据
     */
    private Object data;
}
