package com.zhl.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-13 15:24
 */
@Slf4j
public class PropertiesFileUtil {
    public PropertiesFileUtil() {
    }

    public static Properties readFileProperties(String fileName) {
        // 获取当前项目文件编译后的根路径 "file:/D:/.../target/classes/"
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if(url != null) {
            // 获取 rpc 的配置文件路径
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try(InputStreamReader inputStreamReader =
                new InputStreamReader(new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }
}
