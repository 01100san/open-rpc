package com.zhl.loadbalance;

import com.zhl.remoting.dto.RpcRequest;

import java.util.List;

/**
 * <p>
 *  请求服务获取服务均衡
 *  负载均衡策略接口
 * @author zhl
 * @since 2024-07-14 10:54
 */
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}
