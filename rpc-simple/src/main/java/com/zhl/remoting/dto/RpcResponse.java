package com.zhl.remoting.dto;

import com.zhl.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 *  注意：使用 Kryo 序列化或反序列化的类必须有对应的无参构造
 * @author zhl
 * @since 2024-07-11 14:25
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;
    /**
     * response code
     */
    private Integer code;
    /**
     * response message
     */
    private String message;
    /**
     * response body
     */
    private T data;

    public static <T>RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        rpcResponse.setMessage(RpcResponseCodeEnum.FAIL.getMessage());
        rpcResponse.setRequestId(requestId);
        if(data!=null) {
            rpcResponse.setData(data);
        }
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

}
