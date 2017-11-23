package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class WeChatInitRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
}
