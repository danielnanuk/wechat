package me.nielcho.wechat.enums;

import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.domain.WeChatMessage;

public enum MessageType {
    TEXT(1, 0, 0, "文本"),
    IMAGE(3, 0, 0, "图片"),
    VOICE(34, 0, 0, "语音"),
    CARD(42, 0, 0, "名片"),
    VIDEO(43, 0, 0, "视频"),
    EMOTION(47, 0, 0, "表情"),
    SPECIAL_EMOTION(49, 0, 8, "特殊表情"),
    FILE(49, 0, 6, "文件"),
    INIT(51, 0, 0, "初始化"),
    LOCATION(1, 48, 0, "位置"),
    SHARE_LINK(49, 0, 5, "分享链接"),
    SYSTEM(10000, 0, 0, "系统");
    private int msgType;
    private int subMsgType;
    private int appMsgType;
    private String name;

    MessageType(int msgType, int subMsgType, int appMsgType, String name) {
        this.msgType = msgType;
        this.subMsgType = subMsgType;
        this.appMsgType = appMsgType;
        this.name = name;
    }

    public boolean match(int msgType, int subMsgType, int appMsgType) {
        return this.msgType == msgType && this.subMsgType == subMsgType && this.appMsgType == appMsgType;
    }

    public boolean match(WeChatMessage message) {
        return match(message.getMsgType(), message.getSubMsgType(), message.getAppMsgType());
    }


    public WeChatMessage newMessage() {
        WeChatMessage weChatMessage = new WeChatMessage();
        weChatMessage.setAppMsgType(appMsgType);
        weChatMessage.setMsgType(msgType);
        weChatMessage.setSubMsgType(subMsgType);
        return weChatMessage;
    }

    public static WeChatConstants.MessageType getMessageType(int msgType, int subMsgType, int appMsgType) {
        for (WeChatConstants.MessageType messageType : WeChatConstants.MessageType.values()) {
            if (messageType.match(msgType, subMsgType, appMsgType)) {
                return messageType;
            }
        }
        return null;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getSubMsgType() {
        return subMsgType;
    }

    public int getAppMsgType() {
        return appMsgType;
    }

    public String getName() {
        return name;
    }
}