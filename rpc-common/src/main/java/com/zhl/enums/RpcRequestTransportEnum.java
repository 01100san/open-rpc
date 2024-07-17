package com.zhl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-17 8:44
 */
@AllArgsConstructor
@Getter
public enum RpcRequestTransportEnum {
    NETTY("netty"),
    SOCKET("socket");
    private final String name;
}
