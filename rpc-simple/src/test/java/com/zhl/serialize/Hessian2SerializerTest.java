package com.zhl.serialize;

import com.zhl.remoting.dto.RpcRequest;
import com.zhl.serialize.hessian.Hessian2Serializer;
import org.junit.jupiter.api.Test;

import java.util.UUID;


/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-19 22:05
 */
public class Hessian2SerializerTest {

    @Test
    public void hessianSerializerTest() {
        RpcRequest target = RpcRequest.builder().methodName("hello")
            .parameters(new Object[]{"sayhelooloo", "sayhelooloosayhelooloo"})
            .interfaceName("github.javaguide.HelloService")
            .paramTypes(new Class<?>[]{String.class, String.class})
            .requestId(UUID.randomUUID().toString())
            .group("group1")
            .version("version1")
            .build();
        Hessian2Serializer hessianSerializer = new Hessian2Serializer();
        byte[] bytes = hessianSerializer.serialize(target);
        RpcRequest actual = hessianSerializer.deserialize(bytes, RpcRequest.class);
        System.out.println("actual = " + actual);
    }

    @Test
    void test(){
        Hessian2Serializer hessianSerializer = new Hessian2Serializer();
        Person person = new Person();
        byte[] bytes = hessianSerializer.serialize(person);
        for (byte aByte : bytes) {
            System.out.println(aByte);
        }
        Person deserialize = hessianSerializer.deserialize(bytes, Person.class);
        System.out.println("deserialize = " + deserialize);
    }
}
