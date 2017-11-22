package me.nielcho.wechat.response;

import lombok.Data;

@Data
public class BaseResponse {
    int Ret;
    String ErrMsg;
}
