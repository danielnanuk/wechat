package me.nielcho.wechat.request;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class OpenStatusNotifyRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "Code")
    int Code;
    @JSONField(name = "FromUserName")
    String FromUserName;
    @JSONField(name = "ToUserName")
    String ToUserName;
    @JSONField(name = "ClientMsgId")
    long ClientMsgId;
}
