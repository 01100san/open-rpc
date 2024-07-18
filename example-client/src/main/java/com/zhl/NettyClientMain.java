package com.zhl;

import com.zhl.config.RpcServiceConfig;
import com.zhl.proxy.RpcClientProxy;
import com.zhl.remoting.transport.RpcRequestTransport;
import com.zhl.remoting.transport.netty.client.NettyRpcClient;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-18 17:17
 */
public class NettyClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
            .version("version1")
            .group("test1")
            .build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println("hello = " + hello);
    }
}
