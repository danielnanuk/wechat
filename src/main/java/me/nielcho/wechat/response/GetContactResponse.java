package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetContactResponse {

//    @JsonProperty("Alias")
//    private String Alias;
//    @JsonProperty("AppAccountFlag")
//    private int AppAccountFlag;
//    @JsonProperty("AttrStatus")
//    private long AttrStatus;
//    @JsonProperty("ChatRoomId")
//    private int ChatRoomId;
    @JsonProperty("City")
    private String City;
//    @JsonProperty("ContactFlag")
//    private int ContactFlag;
//    @JsonProperty("DisplayName")
//    private String DisplayName;
    @JsonProperty("EncryChatRoomId")
    private String EncryChatRoomId;
    @JsonProperty("HeadImgUrl")
    private String HeadImgUrl;
//    @JsonProperty("HideInputBarFlag")
//    private int HideInputBarFlag;
//    @JsonProperty("IsOwner")
//    private int IsOwner;
//    @JsonProperty("KeyWord")
//    private String KeyWord;
    @JsonProperty("MemberCount")
    private int MemberCount;
    @JsonProperty("MemberList")
    private List<Member> MemberList;
    @JsonProperty("NickName")
    private String NickName;
//    @JsonProperty("OwnerUin")
//    private int OwnerUin;
    @JsonProperty("PYInitial")
    private String PYInitial;
//    @JsonProperty("PYQuanPin")
//    private String PYQuanPin;
//    @JsonProperty("Province")
//    private String Province;
    @JsonProperty("RemarkName")
    private String RemarkName;
    @JsonProperty("RemarkPYInitial")
    private String RemarkPYInitial;
//    @JsonProperty("RemarkPYQuanPin")
//    private String RemarkPYQuanPin;
    @JsonProperty("Sex")
    private int Sex;
    @JsonProperty("Signature")
    private String Signature;
//    @JsonProperty("SnsFlag")
//    private int SnsFlag;
//    @JsonProperty("StarFriend")
//    private int StarFriend;
//    @JsonProperty("Statues")
//    private int Statues;
//    @JsonProperty("Uin")
//    private int Uin;
//    @JsonProperty("UniFriend")
//    private int UniFriend;
    @JsonProperty("UserName")
    private String UserName;
    @JsonProperty("VerifyFlag")
    private int VerifyFlag;
}
