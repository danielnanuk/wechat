package me.nielcho.wechat.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String MsgId;
    private String FromUserName;
    private String ToUserName;
    private int MsgType;
    private String Content;
    //private long Status;
    //private int ImgStatus;
    private long CreateTime;
    //private int VoiceLength;
    //private int PlayLength;
    private String FileName;
    private String FileSize;
    private String MediaId;
    private String Url;
    private int AppMsgType;
    private int StatusNotifyCode;
    //private String StatusNotifyUserName;
    private String RecommendInfo;
    //private int ForwardFlag;
    //private int HasProductId;
    //private String Ticket;
    //private int ImgHeight;
    //private int ImgWidth;
    private int SubMsgType;
    //private String NewMsgId;
    //private String OriContent;
    //private AppInfo AppInfo;
}
