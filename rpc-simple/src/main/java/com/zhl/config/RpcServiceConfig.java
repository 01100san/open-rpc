package com.zhl.config;

import lombok.*;

/**
 * <p>
 *  rpcServiceName=interfaceName（全限定路径）+group+version
 * @author zhl
 * @since 2024-07-15 14:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RpcServiceConfig {
    private String version="";
    /**
     * 当接口有多个实现类时，按组区分
     */
    private String group="";
    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return  this.getServiceName() + this.getGroup() + this.getVersion();
    }
    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
