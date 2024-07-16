package com.zhl.loadbalance;

import com.zhl.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * <p>
 *  随机负载均衡算法
 * @author zhl
 * @since 2024-07-14 11:12
 */
public class RandomLoadBalance extends AbstractLoadBalance{
    @Override
    protected String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
