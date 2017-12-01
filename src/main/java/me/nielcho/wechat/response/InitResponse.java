package me.nielcho.wechat.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class InitResponse extends BaseWxResponse {
    
//    int Count;
    String ChatSet;
//    int ClickReportInterval;
//    int ClientVersion;
//    List<GetContactResponse> ContactList;
//    int GrayScale;
//    int InviteStartCount;
//    int MPSubscribeMsgCount;
//    String SKey;
    SyncKey SyncKey;
//    Long SystemTime;
    UserResponse User;
}
