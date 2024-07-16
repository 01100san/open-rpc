package com.zhl.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * <p>
 *  定义传输实体类的客户端请求
 *  注意：使用 Kryo 序列化或反序列化的类必须有对应的无参构造
 *
 *
 * @author zhl
 * @since 2024-07-11 14:23
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    /**
     * 全限定接口名
     */
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    /**
     * group 字段主要用于处理一个接口有多个类实现的情况。
     */
    private String group;
    /**
     *  version 字段（服务版本）主要是为后续不兼容升级提供可能。
     */
    private String version;

    /**
     * interfaceName + group + version
     * @return rpc 服务名称
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
