package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class BaseRequest {
    @JSONField(name = "Uin")
    private Long Uin;
    @JSONField(name = "Sid")
    private String Sid;
    @JSONField(name = "Skey")
    private String Skey;
    @JSONField(name = "DeviceID")
    private String DeviceID;
}
