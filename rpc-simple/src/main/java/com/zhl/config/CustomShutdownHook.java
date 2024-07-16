package com.zhl.config;

import com.zhl.registry.zk.util.CuratorUtils;
import com.zhl.remoting.constants.RpcServiceConstant;
import com.zhl.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * <p>
 *  自定义钩子类
 *  在服务端vm关闭时，关闭相应的线程池资源清除相关的服务
 * @author zhl
 * @since 2024-07-15 17:01
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcServiceConstant.SERVICE_PORT));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
