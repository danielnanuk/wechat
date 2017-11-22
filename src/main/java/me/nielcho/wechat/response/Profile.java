package me.nielcho.wechat.response;

import lombok.Data;

@Data
public class Profile {
    Buff UserName;
    String HeadImgUrl;
    int Sex;
    int PersonalCard;
    Buff NickName;
    Buff BindEmail;
    int BitFlag;
    String alias;
    String Signature;
    Long BindUin;
    int HeadImgUpdatedFlag;
    Buff BindMobile;
}
