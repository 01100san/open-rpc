package com.zhl.remoting.transport.netty;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-12 15:41
 */
public class NettyServerTest {
    public static void main(String[] args) {
        new NettyServer(8090).start();
    }
}
