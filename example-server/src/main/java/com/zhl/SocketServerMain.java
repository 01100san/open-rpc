package com.zhl;

import com.zhl.config.RpcServiceConfig;
import com.zhl.remoting.transport.socket.SocketRpcServer;
import com.zhl.service.HelloServiceImpl;
import com.zhl.service.HelloServiceImpl2;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 9:24
 */
public class SocketServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}
