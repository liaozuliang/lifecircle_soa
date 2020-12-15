package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 15:43
 */
@Data
public class InnerRpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    // 200成功，非200失败
    private String code = SUCCESS_CODE;
    private String message = "success";
    private T data;

    private static final String SUCCESS_CODE = "200";
    private static final String FAIL_CODE = "500";

    public InnerRpcResponse() {
    }

    public InnerRpcResponse(T data) {
        this.data = data;
    }

    public InnerRpcResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public InnerRpcResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public static InnerRpcResponse success(Object data) {
        InnerRpcResponse response = new InnerRpcResponse();
        response.setData(data);
        response.setCode(SUCCESS_CODE);
        response.setMessage("操作成功");
        return response;
    }

    public static InnerRpcResponse fail() {
        InnerRpcResponse response = new InnerRpcResponse();
        response.setCode(FAIL_CODE);
        response.setMessage("操作失败");
        return response;
    }

    public static InnerRpcResponse fail(String message) {
        InnerRpcResponse response = new InnerRpcResponse();
        response.setCode(FAIL_CODE);
        response.setMessage(message);
        return response;
    }
}
