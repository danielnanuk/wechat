package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SendMessageResponse extends BaseWxResponse {
    @JsonProperty("LocalID")
    String LocalID;
    @JsonProperty("MsgID")
    String MsgID;
    Long createTime;
    String fileName;
    String url;
    Long fileSize;
    int msgType;
    int appMsgType;
    int subMsgType;
}
