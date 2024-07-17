package com.zhl.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  存储并获取建立连接的 Channel 对象
 * @author zhl
 * @since 2024-07-17 9:50
 */
@Slf4j
public class ChannelProvider {
    /**
     * 存储连接后的 address 和 Channel的缓存，一个 address 对应一个 Channel
     */
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if(channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // channel != null, 并且 channel 已连接且已打开
            if(channel!=null && channel.isActive()) {
                return channel;
            }else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.putIfAbsent(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }
}
