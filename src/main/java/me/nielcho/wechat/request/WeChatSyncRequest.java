package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.nielcho.wechat.response.SyncKey;

@Data
public class WeChatSyncRequest {
    @JsonProperty("BaseRequest")
    BaseRequest baseRequest;
    @JsonProperty("SyncKey")
    SyncKey syncKey;
    @JsonProperty("rr")
    long rr;
    
    
}
