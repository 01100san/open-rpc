package com.zhl.remoting.transport;

import com.zhl.extensions.SPI;
import com.zhl.remoting.dto.RpcRequest;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-15 16:08
 */
@SPI
public interface RpcRequestTransport {
    /**
     * 发送 RPC 请求到服务器并返回结果
     * @param rpcRequest    消息体
     * @return  来自服务端的数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
