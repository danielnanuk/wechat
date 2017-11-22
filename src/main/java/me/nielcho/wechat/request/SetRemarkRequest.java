package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.nielcho.wechat.context.WeChatContext;

@Data
public class SetRemarkRequest {
    
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
    @JsonProperty("UserName")
    String UserName;
    @JsonProperty("RemarkName")
    String RemarkName;
    @JsonProperty("CmdId")
    int CmdId = 2;
    
    
    public static SetRemarkRequest newSetRemarkRequest(WeChatContext context, String to, String remarkName) {
        SetRemarkRequest request = new SetRemarkRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setUserName(to);
        request.setRemarkName(remarkName);
        return request;
    }
}
