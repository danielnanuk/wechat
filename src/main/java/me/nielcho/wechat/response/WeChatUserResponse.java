package me.nielcho.wechat.response;


import lombok.Data;

@Data
public class WeChatUserResponse {
    private int AppAccountFlag;
    private int ContactFlag;
    private int HeadImgFlag;
    private String HeadImgUrl;
    private int HideInputBarFlag;
    private String NickName;
    private String PYInitial;
    private String PYQuanPin;
    private String RemarkName;
    private String RemarkPYInitial;
    private String RemarkPYQuanPin;
    private int Sex;
    private String Signature;
    private int SnsFlag;
    private int StarFriend;
    private long Uin;
    private String UserName;
    private int VerifyFlag;
    private int WebWxPluginSwitch;
}
