package com.zhl.serialize;

import com.zhl.remoting.dto.RpcResponse;
import com.zhl.serialize.kryo.KryoSerializer;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-12 10:47
 */
public class KryoSerializerTest {
    @Test
    public void testSerializer(){
        RpcResponse response = new RpcResponse("hello", 200, "hello", "");
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serialize = kryoSerializer.serialize(response);
        System.out.println("serialize = " + serialize);
        RpcResponse deserialize = kryoSerializer.deserialize(serialize, RpcResponse.class);
        System.out.println("deserialize = " + deserialize);
    }
    @Test
    public void testInetSocketAddress() throws UnknownHostException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 2011);
        System.out.println("inetSocketAddress = " + inetSocketAddress.toString());
    }


}
