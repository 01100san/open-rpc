package com.zhl.exception;

import com.zhl.enums.RpcErrorMessageEnum;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-14 15:44
 */
public class RpcException extends RuntimeException{

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage()+":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
