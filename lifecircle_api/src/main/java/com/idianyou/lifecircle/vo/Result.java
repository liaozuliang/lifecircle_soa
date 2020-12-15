package com.idianyou.lifecircle.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 与前端app交互的接口返回对象
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/21 17:30
 */
@Getter
@Setter
public class Result<T> {

    private String resultCode;

    private String message;

    @JsonProperty("Data")
    private T data;

    public boolean isSuccess() {
        return "200".equals(resultCode);
    }

    public static Result success() {
        Result result = new Result();
        result.setResultCode("200");
        result.setMessage("成功");
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setResultCode("200");
        result.setMessage("成功");
        result.setData(data);
        return result;
    }

    public static Result failed() {
        Result result = new Result();
        result.setResultCode("201");
        result.setMessage("失败");
        return result;
    }

    public static Result failed(String errorCode, String errorMessage) {
        if (StringUtils.isBlank(errorCode) || "200".equals(errorCode)) {
            errorCode = "501";
        }

        Result result = new Result();
        result.setResultCode(errorCode);
        result.setMessage(errorMessage);

        return result;
    }

    public static Result failed(Object data) {
        Result result = new Result();
        result.setResultCode("201");
        result.setMessage("失败");
        result.setData(data);
        return result;
    }

    public static Result notLogin() {
        Result result = new Result();
        result.setResultCode("301");
        result.setMessage("用户未登录");
        return result;
    }

    public static Result paramError(String paramName) {
        Result result = new Result();
        result.setResultCode("901");
        result.setMessage("参数" + paramName + "错误");
        return result;
    }

    public static Result systemError() {
        Result result = new Result();
        result.setResultCode("500");
        result.setMessage("服务器异常,请稍后再试");
        return result;
    }

    public static Result clientVersionTooLow() {
        Result result = new Result();
        result.setResultCode("501");
        result.setMessage("客户端版本过低，请升级到最新版本");
        return result;
    }
}
