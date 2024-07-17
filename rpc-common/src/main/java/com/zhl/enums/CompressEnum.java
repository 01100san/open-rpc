package com.zhl.enums;

import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-17 16:52
 */
@AllArgsConstructor
@Getter
public enum CompressEnum {
    GZIP((byte) 0x01,"gzip" );
    private final byte code;
    private final String name;

    public static String getName(byte compressType) {
        for (CompressEnum item : CompressEnum.values()) {
            if (item.getCode()== compressType) {
                return item.getName();
            }
        }
        return null;
    }
}
