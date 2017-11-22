package me.nielcho.wechat.domain;

import lombok.Data;
import me.nielcho.wechat.constants.WeChatConstants;

@Data
public class WeChatMessage {
    String fromGroup;
    String fromUser;
    String toUser;
    String fromGroupUserName;
    String fromUserName;
    String toUserName;
    int msgType;
    int subMsgType;
    int appMsgType;
    String msgId;
    String mediaId;
    String thumbnail;
    String url;
    String content;
    String title;
    String remarkName;
    String fileName;
    long fileSize;
    WeChatConstants.MessageDirection direction;
    boolean groupMessage;
    long createTime;
    String chatroomId;
    String headImg;
}

