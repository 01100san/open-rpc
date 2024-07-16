package com.zhl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *  rpc 的配置枚举类
 * @author zhl
 * @since 2024-07-13 15:50
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    /**
     * 配置文件为 rpc.properties
     */
    RPC_CONFIG_PATH("rpc.properties"),
    /**
     *  zookeeper 注册中心配置地址为
     */
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
