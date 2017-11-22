package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeChatInitRequest {
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
}
