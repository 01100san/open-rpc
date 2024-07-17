package com.zhl.remoting.transport.netty.client;

import com.zhl.remoting.dto.RpcResponse;
import org.w3c.dom.css.ViewCSS;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  处理未被处理的请求
 *  这里封装了 CompletableFuture 进行了优化，EventLoop 不再阻塞等待 rpcResponse 的响应，且可以在任意地方获取 future 处理的 响应对象。
 * @author zhl
 * @since 2024-07-17 14:19
 */
public class UnprocessedRequests {
    /**
     * key: requestId
     * value:RpcResponse
     * 根据 rpcRequest.getRequestId 获取对应的 future 处理的 RpcResponse 对象
     */
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * 处理对应的 Rpc 请求，并移出Map缓存
     */
    public void compute(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> completableFuture = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if(completableFuture != null) {
            completableFuture.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
