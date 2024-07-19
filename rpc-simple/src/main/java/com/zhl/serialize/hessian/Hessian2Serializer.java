package com.zhl.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.zhl.exception.SerializeException;
import com.zhl.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>
 *  封装 Hessian 序列化和反序列化的工具类
 *      Hessian 是一种动态类型、二进制序列化和 Web 服务协议，专为面向对象传输而设计。
 *      注意：使用 Hessian 序列化的对象必须实现  java.io.Serializable 接口
 * @author zhl
 * @since 2024-07-19 22:00
 */
public class Hessian2Serializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            Hessian2Output output = new Hessian2Output(baos);
            output.writeObject(obj);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Hessian2Input input = new Hessian2Input(bais);
            return clazz.cast(input.readObject());
        } catch (Exception e) {
            throw new SerializeException(e.getMessage());
        }
    }
}
