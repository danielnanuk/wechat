package me.nielcho.wechat.constants;

import java.util.regex.Pattern;

public class WeChatConstants {

    public static final int SESSION_STATE_LOGGING = 1;
    public static final int SESSION_STATE_LOGINED = 2;

    public static final Pattern UUID_PATTERN = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\"");
    public static final Pattern SYNC_CHECK_PATTERN = Pattern.compile("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"}");
    public static final Pattern LOGIN_CODE_PATTERN = Pattern.compile("window.code=(\\d+);");
    public static final Pattern LOGIN_REDIRECT_PATTERN = Pattern.compile("window.redirect_uri=\"(\\S+?)\"");
    public static final Pattern USER_AVATAR_PATTERN = Pattern.compile("window.userAvatar = '(\\S+?)'");
    public static final String WX_FILEHELPER = "filehelper";

    public static final String WX_GET_GROUP_ICON = "/cgi-bin/mmwebwx-bin/webwxgetheadimg";
    public static final String WX_GET_ICON = "/cgi-bin/mmwebwx-bin/webwxgeticon";
    public static final String WX_SYNC_URL = "/cgi-bin/mmwebwx-bin/webwxsync?sid=%s&skey=%s&pass_ticket=%s&lang=%s";
    public static final String WX_SYNC_CHECK_URL = "/cgi-bin/mmwebwx-bin/synccheck?r=%s&skey=%s&sid=%s&uin=%s&deviceid=%s&synckey=%s&_=%s";
    public static final String WX_INIT_URL = "/cgi-bin/mmwebwx-bin/webwxinit?r=%s&lang=%s&pass_ticket=%s&skey=%s";
    public static final Integer STATUS_CODE_READ = 1;
    public static final String WX_STATUS_REPORT_URL = "/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=%s&pass_ticket=%s";
    public static final String DEFAULT_REDIRECT_URI = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage";


    public static final String SEND_FILE_APPID = "wxeb7ec651dd0aefa9";
    public static final String WEB_WX_APPID = "wx782c26e4c19acffb";
    public static final String LANG = "zh_CN";

    public static final String SCANNED = "0";
    public static final String NOT_SCANNED = "1";


    public static final String WX_LOGIN_REQUEST_URL = "https://login.wx.qq.com/jslogin";
    public static final String WX_CHECK_LOGIN_URL = "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login";

    public static final String WX_OPEN_STATUS_NOTIFY_URL = "/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=%s&pass_ticket=%s";
    public static final int WX_OPEN_STATUS_NOTIFY_CODE = 3;

    public static final String WX_SEND_TEXT_URL = "/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket=%s";

    public static final String WX_SEND_IMAGE_URL = "/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json&pass_ticket=%s";

    public static final String WX_SEND_FILE_URL = "/cgi-bin/mmwebwx-bin/webwxsendappmsg?fun=async&f=json";
    public static final String WX_SEND_FILE_TEMPLATE = "<appmsg appid='%s' sdkver=''><title>%s</title><des></des><action></action><type>6</type><content></content><url></url><lowurl></lowurl><appattach><totallen>%d</totallen><attachid>%s</attachid><fileext>%s</fileext></appattach><extinfo></extinfo></appmsg>";

    public static final String WX_SET_REMARK_URL = "/cgi-bin/mmwebwx-bin/webwxoplog?lang=zh_CN";

    public static final String WX_GET_CONTACT_URL = "/cgi-bin/mmwebwx-bin/webwxgetcontact?lang=%s&r=%s&seq=%s&skey=%s";

    public static final String WX_GET_IMAGE_URL = "/cgi-bin/mmwebwx-bin/webwxgetmsgimg?MsgId=%s&skey=%s";

    public static final String WX_GET_VOICE_URL = "/cgi-bin/mmwebwx-bin/webwxgetvoice?MsgId=%s&skey=%s";

    public static final String WX_LOGOUT_URL = "/cgi-bin/mmwebwx-bin/webwxlogout?redirect=0&type=0&skey=%s";

    public static final String WX_UPLOAD_MEDIA_URL = "/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";

    public static final String WX_GET_BATCH_CONTACT_URL = "/cgi-bin/mmwebwx-bin/webwxbatchgetcontact?type=ex&r=%s&lang=%s";

    public static final String WX_GET_MEDIA_URL = "/cgi-bin/mmwebwx-bin/webwxgetmedia";

    public static final String WX_GET_VIDEO_URL = "/cgi-bin/mmwebwx-bin/webwxgetvideo?msgid=%s&skey=%s";

    public static final String WX_GET_LOCATION_IMAGE_URL = "/cgi-bin/mmwebwx-bin/webwxgetpubliclinkimg?url=%s&msgid=%s&pictype=location";

    public static final String WX_ADD_CONTACT_URL = "/cgi-bin/mmwebwx-bin/webwxverifyuser?r=%s&lang=%s&pass_ticket=%s";

    public static final String WX_LOGIN_QRCODE = "https://login.weixin.qq.com/l/";

    public static final String WX_STAT_REPORT = "/cgi-bin/mmwebwx-bin/webwxstatreport?fun=new&pass_ticket=%s";

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

        public static MessageType getMessageType(int msgType, int subMsgType, int appMsgType) {
            for (MessageType messageType : MessageType.values()) {
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

    public enum MessageDirection {
        SEND("发送"), RECEIVE("收到");

        private String desc;

        MessageDirection(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}