package com.zhl.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * <p>
 * 使用 Socket 编程
 *      模拟 服务端
 * @author zhl
 * @since 2024-07-11 10:41
 */
@Slf4j
public class SocketClient {
    public void send(String message, String hostname, int port) throws IOException {
        try(Socket socket = new Socket(hostname, port)) {
            if (socket.isConnected()) {
                // 向服务端发送信息
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(message);
                oos.flush();

                log.info("数据发送完毕");

                // 读取服务端发送的信息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 读取服务端发送的信息
                message = (String) ois.readObject();
                log.info("收到服务端的消息: {}", message);

            }
        } catch (Exception e) {
            log.error("exception ", e);
        }

    }
}
