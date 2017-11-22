package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetBatchUserName {
    @JsonProperty("UserName")
    String UserName;
    @JsonProperty("EncryChatRoomId")
    String EncryChatRoomId;
    
    
    public static GetBatchUserName fromUserName(String userName) {
        GetBatchUserName getBatchUserName = new GetBatchUserName();
        getBatchUserName.setUserName(userName);
        getBatchUserName.setEncryChatRoomId("");
        return getBatchUserName;
    }
}
