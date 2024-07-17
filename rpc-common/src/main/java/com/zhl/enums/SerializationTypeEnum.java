package com.zhl.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-17 16:57
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KRYO((byte) 0x01, "kryo");

    private final byte code;
    private final String name;

    public static String getName(byte serializeType) {
        for (SerializationTypeEnum item : SerializationTypeEnum.values()) {
            if (item.getCode()== serializeType) {
                return item.getName();
            }
        }
        return null;
    }
}
