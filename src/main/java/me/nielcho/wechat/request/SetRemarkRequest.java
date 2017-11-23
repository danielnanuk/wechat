package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import me.nielcho.wechat.context.WeChatContext;

@Data
public class SetRemarkRequest {
    
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "UserName")
    String UserName;
    @JSONField(name = "RemarkName")
    String RemarkName;
    @JSONField(name = "CmdId")
    int CmdId = 2;
    
    
    public static SetRemarkRequest newSetRemarkRequest(WeChatContext context, String to, String remarkName) {
        SetRemarkRequest request = new SetRemarkRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setUserName(to);
        request.setRemarkName(remarkName);
        return request;
    }
}
