package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class GetContactResponse {

//    @JSONField(name = "Alias")
//    private String Alias;
//    @JSONField(name = "AppAccountFlag")
//    private int AppAccountFlag;
//    @JSONField(name = "AttrStatus")
//    private long AttrStatus;
//    @JSONField(name = "ChatRoomId")
//    private int ChatRoomId;
    @JSONField(name = "City")
    private String City;
//    @JSONField(name = "ContactFlag")
//    private int ContactFlag;
//    @JSONField(name = "DisplayName")
//    private String DisplayName;
    @JSONField(name = "EncryChatRoomId")
    private String EncryChatRoomId;
    @JSONField(name = "HeadImgUrl")
    private String HeadImgUrl;
//    @JSONField(name = "HideInputBarFlag")
//    private int HideInputBarFlag;
//    @JSONField(name = "IsOwner")
//    private int IsOwner;
//    @JSONField(name = "KeyWord")
//    private String KeyWord;
    @JSONField(name = "MemberCount")
    private int MemberCount;
    @JSONField(name = "MemberList")
    private List<Member> MemberList;
    @JSONField(name = "NickName")
    private String NickName;
//    @JSONField(name = "OwnerUin")
//    private int OwnerUin;
    @JSONField(name = "PYInitial")
    private String PYInitial;
//    @JSONField(name = "PYQuanPin")
//    private String PYQuanPin;
//    @JSONField(name = "Province")
//    private String Province;
    @JSONField(name = "RemarkName")
    private String RemarkName;
    @JSONField(name = "RemarkPYInitial")
    private String RemarkPYInitial;
//    @JSONField(name = "RemarkPYQuanPin")
//    private String RemarkPYQuanPin;
    @JSONField(name = "Sex")
    private int Sex;
    @JSONField(name = "Signature")
    private String Signature;
//    @JSONField(name = "SnsFlag")
//    private int SnsFlag;
//    @JSONField(name = "StarFriend")
//    private int StarFriend;
//    @JSONField(name = "Statues")
//    private int Statues;
//    @JSONField(name = "Uin")
//    private int Uin;
//    @JSONField(name = "UniFriend")
//    private int UniFriend;
    @JSONField(name = "UserName")
    private String UserName;
    @JSONField(name = "VerifyFlag")
    private int VerifyFlag;
}
