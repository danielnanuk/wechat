package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SendMessageResponse extends BaseWxResponse {
    @JSONField(name = "LocalID")
    String LocalID;
    @JSONField(name = "MsgID")
    String MsgID;
    Long createTime;
    String fileName;
    String url;
    Long fileSize;
    int msgType;
    int appMsgType;
    int subMsgType;
}
