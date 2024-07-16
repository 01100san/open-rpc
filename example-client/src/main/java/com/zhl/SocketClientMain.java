package com.zhl;

import com.zhl.config.RpcServiceConfig;
import com.zhl.proxy.RpcClientProxy;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.remoting.transport.RpcRequestTransport;
import com.zhl.remoting.transport.socket.SocketRpcClient;
import io.protostuff.Rpc;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 9:32
 */
public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        for (int i = 0; i < 10; i++) {
            RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
            HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
            String hello = helloService.hello(new Hello("333", "444"));
            System.out.println(hello);
        }
    }
}
