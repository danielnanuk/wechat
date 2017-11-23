package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SendMediaRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "Msg")
    Msg Msg;
    @JSONField(name = "Scene")
    Integer Scene;
}
