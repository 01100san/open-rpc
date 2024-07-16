package com.zhl.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 *  使用 Socket 编程
 *      模拟 服务端
 * @author zhl
 * @since 2024-07-11 10:41
 */
@Slf4j
public class SocketServer {
    public void start(int port)  {
        try(ServerSocket ss = new ServerSocket(port)) {
                // 监听客户端连接事件
                Socket socket;
                log.info("waiting...");
                while ((socket =ss.accept()) != null) {
                    log.info("client connected");
                    try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                        // 通过输入流, 读取客户端发送的信息
                        String message = (String) ois.readObject();
                        log.info("message = {}", message);

                        // 通过输出流向客户端返回信息
                        message+=" accept ok! ";
                        oos.writeObject(message);
                        oos.flush();
                    }catch (ClassNotFoundException e) {
                        log.error("exception ", e);
                    }
                }
        } catch (IOException e) {
            log.error("IOException ", e);
        }
    }
}
