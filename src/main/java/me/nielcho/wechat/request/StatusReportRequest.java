package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatusReportRequest  {
    @JsonProperty("BaseRequest")
    private BaseRequest baseRequest;
    @JsonProperty("ClientMsgId")
    private long clientMsgId;
    @JsonProperty("Code")
    private int code;
    @JsonProperty("FromUserName")
    private String fromUserName;
    @JsonProperty("ToUserName")
    private String toUserName;
}
