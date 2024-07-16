package com.zhl.remoting.transport;

import com.zhl.remoting.dto.RpcRequest;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-15 16:08
 */
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
