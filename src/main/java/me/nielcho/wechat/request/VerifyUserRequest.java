package me.nielcho.wechat.request;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class VerifyUserRequest {
    @JSONField(name = "Value")
    String Value;
    @JSONField(name = "VerifyUserTicket")
    String VerifyUserTicket = "";
}
