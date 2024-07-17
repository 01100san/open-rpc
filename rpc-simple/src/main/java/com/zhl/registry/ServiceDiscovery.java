package com.zhl.registry;

import com.zhl.extensions.SPI;
import com.zhl.remoting.dto.RpcRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * <p>
 *  服务发现
 * @author zhl
 * @since 2024-07-14 9:25
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 根据 RpcRequest 的 rpcServiceName 获取服务，通过负载均衡获取一个服务
     * @param rpcRequest    获取服务的请求
     * @return  返回一个服务的地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
