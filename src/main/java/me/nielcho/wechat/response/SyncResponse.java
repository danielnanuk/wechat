package me.nielcho.wechat.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class SyncResponse extends BaseWxResponse {
    
    //int continueFlag;
    //String SKey;
    
    int ModContactCount;
    List<ModContact> ModContactList;
    int DelContactCount;
    List<DelContact> DelContactList;
    //int ModChatRoomMemberCount;
    //List<JSONObject> ModChatRoomMemberList;
    int AddMsgCount;
    List<MessageResponse> AddMsgList;
    
    //Profile Profile;
    //SyncKey SyncCheckKey;
    SyncKey SyncKey;
    
}
