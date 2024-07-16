package com.zhl.loadbalance;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.utils.CollectionUtil;

import java.util.List;

/**
 * <p>
 *  抽象负载均衡
 * @author zhl
 * @since 2024-07-14 11:08
 */
public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        }
        if(serviceAddresses.size()==1) {
            return serviceAddresses.get(0);
        }else {
            return doSelect(serviceAddresses, rpcRequest);
        }
    }

    protected abstract String doSelect(List<String> serviceAddress, RpcRequest rpcRequest);

}
