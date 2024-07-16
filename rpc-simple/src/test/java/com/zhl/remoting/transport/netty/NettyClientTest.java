package com.zhl.remoting.transport.netty;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-12 15:41
 */
public class NettyClientTest {
    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder().
                                interfaceName("interface").
                                methodName("hello").build();
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8090);
        for (int i = 0; i < 3; i++) {
            RpcResponse response = nettyClient.sendMessage(rpcRequest);
            System.out.println("response = " + response);
        }
        System.out.println();
        RpcResponse response = nettyClient.sendMessage(rpcRequest);
        System.out.println("response = " + response);
    }
}
