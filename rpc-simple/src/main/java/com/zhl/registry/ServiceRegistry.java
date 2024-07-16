package com.zhl.registry;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * <p>
 *  服务注册
 * @author zhl
 * @since 2024-07-14 9:25
 */
public interface ServiceRegistry {
    /**
     * 服务注册
     * @param rpcServiceName    interfaceName + group + version
     * @param inetAddress       向注册中心注册的服务地址
     */
    void registryService(String rpcServiceName , InetSocketAddress inetAddress);
}
