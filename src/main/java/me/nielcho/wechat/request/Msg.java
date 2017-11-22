package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.util.WeChatUtil;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Msg {
    @JsonProperty("Type")
    int Type;
    @JsonProperty("FromUserName")
    String FromUserName;
    @JsonProperty("ToUserName")
    String ToUserName;
    @JsonProperty("LocalID")
    String LocalID;
    @JsonProperty("Content")
    String Content = "";
    @JsonProperty("ClientMsgId")
    String ClientMsgId;
    @JsonProperty("MediaId")
    String MediaId;
    
    public static Msg newTextMsg(WeChatContext context, String to, String content) {
        String clientMsgId = WeChatUtil.generateClientMsgId();
        Msg msg = new Msg();
        msg.setContent(content);
        msg.setFromUserName(context.getUser().getUserName());
        msg.setToUserName(to);
        msg.setLocalID(clientMsgId);
        msg.setClientMsgId(clientMsgId);
        msg.setType(WeChatConstants.MessageType.TEXT.getMsgType());
        return msg;
    }
    
    public static Msg newImageMsg(WeChatContext context, String to, String mediaId) {
        String clientMsgId = WeChatUtil.generateClientMsgId();
        Msg msg = new Msg();
        msg.setMediaId(mediaId);
        msg.setFromUserName(context.getUser().getUserName());
        msg.setToUserName(to);
        msg.setLocalID(clientMsgId);
        msg.setClientMsgId(clientMsgId);
        msg.setType(WeChatConstants.MessageType.IMAGE.getMsgType());
        return msg;
    }
    
    public static Msg newFileMsg(WeChatContext context, String to, String mediaId) {
        String clientMsgId = WeChatUtil.generateClientMsgId();
        Msg msg = new Msg();
        msg.setMediaId(mediaId);
        msg.setFromUserName(context.getUser().getUserName());
        msg.setToUserName(to);
        msg.setLocalID(clientMsgId);
        msg.setClientMsgId(clientMsgId);
        msg.setType(WeChatConstants.MessageType.FILE.getMsgType());
        return msg;
    }
}
