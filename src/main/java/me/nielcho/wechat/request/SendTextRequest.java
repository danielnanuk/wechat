package me.nielcho.wechat.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class SendTextRequest {
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
    @JsonProperty("Msg")
    Msg Msg;
    
}
