package com.zhl.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zhl.exception.SerializeException;
import com.zhl.remoting.dto.RpcRequest;
import com.zhl.remoting.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * <p>
 *  封装 Kryo 序列化和反序列化的工具类
 * @author zhl
 * @since 2024-07-12 9:56
 */
@Slf4j
public class KryoSerializer implements Serializer{

    /**
     * Kryo 的序列化不是线程安全的，每个线程都应该有自己的 Kryo，Input 和 Output 示例，将Kryo 存储在线程中
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 默认值为true,是否关闭注册行为,关闭之后可能存在序列化问题，一般推荐设置为 true
        kryo.setReferences(true);
        // 默认值为false,是否关闭循环引用，可以提高性能，但是一般不推荐设置为 true
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos)) {
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais)) {
            Kryo kryo = kryoThreadLocal.get();
            T obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return obj;
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
