package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class StatusReportRequest  {
    @JSONField(name = "BaseRequest")
    private BaseRequest baseRequest;
    @JSONField(name = "ClientMsgId")
    private long clientMsgId;
    @JSONField(name = "Code")
    private int code;
    @JSONField(name = "FromUserName")
    private String fromUserName;
    @JSONField(name = "ToUserName")
    private String toUserName;
}
