package me.nielcho.wechat.response;

import lombok.Data;

import java.util.List;


@Data
public class ModContact {
    String UserName;
    
//    int ContactType;
    List<Member> MemberList;
    String HeadImgUrl;
    int Sex;
//    int MemberCount;
//    long AttrStatus;
//    int Statues;
    String City;
    String NickName;
//    String Province;
//    int SnsFlag;
//    String Alias;
//    String KeyWord;
//    String ChatRoomOwner;
//    int HideInputBarFlag;
    String Signature;
    String RemarkName;
//    int HeadImgUpdateFlag;
//    int VerifyFlag;
    // 目前猜测来看, 1是保存的联系人, 2是未保存的群聊, 3是保存的群聊
//    int ContactFlag;
}
