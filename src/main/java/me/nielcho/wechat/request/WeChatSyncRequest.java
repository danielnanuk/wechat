package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import me.nielcho.wechat.response.SyncKey;

@Data
public class WeChatSyncRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest baseRequest;
    @JSONField(name = "SyncKey")
    SyncKey syncKey;
    @JSONField(name = "rr")
    long rr;
    
    
}
