package com.zhl.registry.zk.util;

import com.sun.xml.internal.bind.v2.TODO;
import com.zhl.enums.RpcConfigEnum;
import com.zhl.utils.PropertiesFileUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  封装 客户端 Curator 操作 Zookeeper 的工具类
 * @author zhl
 * @since 2024-07-13 8:51
 */
public class CuratorUtils {
    private static final Logger log = LoggerFactory.getLogger(CuratorUtils.class);
    /**
     * 重试策略中的重试间隔时间
     */
    private static final int BASE_SLEEP_TIME = 1000;
    /**
     * 重试策略中的最大重试次数
     */
    private static final int MAX_RETRIES = 3;
    /**
     * zookeeper 的默认地址
     */
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    /**
     * 添加已注册的持久节点
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    /**
     * 根据rpcServiceName 存储可能存在的多个服务地址
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";

    private static CuratorFramework zkClient;


    /**
     * 获取 当前服务名下的所有服务节点
     *  如果在 服务哈希表缓存中直接取出，否则加入，并添加注册器
     * @param zkClient
     * @param rpcServiceName
     * @return  服务地址
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            // 将当前 rpcServiceName 注册 Watch 机制
            registerWatcher(zkClient, rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * 创建持久节点
     * @param path  节点路径    尾部是 InetAddress.toString()
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path)!=null) {
                log.info("The node already exists. The node is:[{}]", path);
            }else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * 清除 zookeeper 中的持久节点
     * @param zkClient
     * @param inetSocketAddress   根据注册节点尾部路径匹配
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p->{
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }


    /**
     * 获取一个 zkClient
     * @return
     */
    public static CuratorFramework getZkClient() {
        Properties properties = PropertiesFileUtil.readFileProperties(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null
                                ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue())
                                : DEFAULT_ZOOKEEPER_ADDRESS;

        // if zkClient has been started, return directly
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
            // 要连接的 zookeeper 地址 "ip:port"
            .connectString(zookeeperAddress)
            // 重试策略，重试三次，重试等待时间 1000ms
            .retryPolicy(retryPolicy)
            .build();
        zkClient.start();
        try {
            // 超过 30s 仍未连接到 zookeeper 抛出异常
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    private static void registerWatcher(CuratorFramework zkClient, String rpcServiceName) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
