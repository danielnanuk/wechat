package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class GetBatchUserName {
    @JSONField(name = "UserName")
    String UserName;
    @JSONField(name = "EncryChatRoomId")
    String EncryChatRoomId;
    
    
    public static GetBatchUserName fromUserName(String userName) {
        GetBatchUserName getBatchUserName = new GetBatchUserName();
        getBatchUserName.setUserName(userName);
        getBatchUserName.setEncryChatRoomId("");
        return getBatchUserName;
    }
}
