package com.zhl.remoting.transport.socket;

import com.zhl.factory.SingletonFactory;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import com.zhl.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-15 16:31
 */
@Slf4j
public class SocketRpcServerRunnable implements Runnable{
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;

    public SocketRpcServerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // 获取客户端的 RpcRequest 请求
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            oos.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }

    }
}
