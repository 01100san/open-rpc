package com.zhl.provider;

import com.zhl.config.RpcServiceConfig;

/**
 * <p>
 *  服务提供者的包装类
 * @author zhl
 * @since 2024-07-15 14:56
 */
public interface ServiceProvider {
    void addService(RpcServiceConfig rpcServiceConfig);
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);
}
