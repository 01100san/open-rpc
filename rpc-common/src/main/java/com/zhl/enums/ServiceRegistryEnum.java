package com.zhl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 22:20
 */
@AllArgsConstructor
@Getter
public enum ServiceRegistryEnum {
    ZK("zk");
    private final String name;
}
