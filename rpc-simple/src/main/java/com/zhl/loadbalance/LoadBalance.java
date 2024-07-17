package com.zhl.loadbalance;

import com.zhl.extensions.SPI;
import com.zhl.remoting.dto.RpcRequest;

import java.util.List;

/**
 * <p>
 *  请求服务获取服务均衡
 *  负载均衡策略接口
 * @author zhl
 * @since 2024-07-14 10:54
 */
@SPI
public interface LoadBalance {
    /**
     * 从现有的服务地址列表中选择一个
     * @param serviceAddresses  现有的服务地址列表
     * @param rpcRequest
     * @return  目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}
