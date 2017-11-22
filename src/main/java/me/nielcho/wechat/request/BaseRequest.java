package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseRequest {
    @JsonProperty("Uin")
    private Long Uin;
    @JsonProperty("Sid")
    private String Sid;
    @JsonProperty("Skey")
    private String Skey;
    @JsonProperty("DeviceID")
    private String DeviceID;
}
