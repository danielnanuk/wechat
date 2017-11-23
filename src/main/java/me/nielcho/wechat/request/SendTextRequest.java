package me.nielcho.wechat.request;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data

public class SendTextRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "Msg")
    Msg Msg;
    
}
