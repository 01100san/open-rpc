package com.zhl.remoting.transport.socket;

import com.zhl.enums.RpcErrorMessageEnum;
import com.zhl.exception.RpcException;
import com.zhl.factory.SingletonFactory;
import com.zhl.registry.ServiceDiscovery;
import com.zhl.registry.zk.ZkServiceDiscoveryImpl;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <p>
 *  基于 Socket 的 rpc 客户端
 * @author zhl
 * @since 2024-07-15 16:06
 */
@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        // TODO SPI
        this.serviceDiscovery = new ZkServiceDiscoveryImpl();
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            socket.connect(address);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 通过输出流把数据发送到 服务端
            oos.writeObject(rpcRequest);
            // 通过输入流接收到服务端发送的数据
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}