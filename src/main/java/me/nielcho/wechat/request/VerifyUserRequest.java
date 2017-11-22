package me.nielcho.wechat.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyUserRequest {
    @JsonProperty("Value")
    String Value;
    @JsonProperty("VerifyUserTicket")
    String VerifyUserTicket = "";
}
