package com.zhl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 22:13
 */
@AllArgsConstructor
@Getter
public enum LoadBalanceEnum {
    LOAD_BALANCE("loadBalance");
    private final String name;
}
