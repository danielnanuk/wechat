package me.nielcho.wechat.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenStatusNotifyRequest {
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
    @JsonProperty("Code")
    int Code;
    @JsonProperty("FromUserName")
    String FromUserName;
    @JsonProperty("ToUserName")
    String ToUserName;
    @JsonProperty("ClientMsgId")
    long ClientMsgId;
}
