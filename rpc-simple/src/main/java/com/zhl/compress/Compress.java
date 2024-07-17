package com.zhl.compress;

import com.zhl.extensions.SPI;

/**
 * <p>
 *  压缩接口
 * @author zhl
 * @since 2024-07-17 16:39
 */
@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
