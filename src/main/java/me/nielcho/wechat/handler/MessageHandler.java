package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Slf4j
public abstract class MessageHandler {

    @Autowired
    protected ContactRepository contactRepository;

    public abstract WeChatConstants.MessageType getSupportedType();

    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {

    }

    public void handle(WeChatContext context, MessageResponse message) {
        WeChatMessage weChatMessage = fromMessageResponse(context, message);
        if (weChatMessage != null) {
            log(context, weChatMessage);
            handleInternal(context, message, weChatMessage);
        }
    }

    String parseXml(String rawContent) {
        return rawContent.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("<br/>", "");
    }

    WeChatMessage fromMessageResponse(WeChatContext context, MessageResponse response) {
        WeChatMessage weChatMessage = new WeChatMessage();
        setBasicInfo(response, weChatMessage);
        String toUserName = response.getToUserName();
        ContactInfo toUser = contactRepository.getContact(context.getUin(), toUserName);
        if (toUser == null) {
            log.info("[*] |{}|{}|:发送公众号消息:{}", context.getId(), context.getUuid(), response);
            return null;
        }
        String fromUserName = response.getFromUserName();
        ContactInfo fromUser = contactRepository.getContact(context.getUin(), fromUserName);
        if (fromUser == null) {
            log.info("[*] |{}|{}|:收到公众号消息:{}", context.getId(), context.getUuid(), response);
            return null;
        }
        boolean isSend = Objects.equals(fromUserName, context.getUser().getUserName());
        boolean isGroupMessage = toUser.isGroup() || fromUser.isGroup();
        weChatMessage.setGroupMessage(isGroupMessage);

        String rawContent = response.getContent();
        weChatMessage.setContent(rawContent);
        if (fromUser.isGroup()) {
            int colonIndex = rawContent.indexOf(':');
            if (weChatMessage.getMsgType() != WeChatConstants.MessageType.SYSTEM.getMsgType() && colonIndex > 0) {
                String fromGroupUserUserName = rawContent.substring(0, colonIndex).trim();
                ContactInfo fromGroup = fromUser;
                fromUser = contactRepository.getContact(context.getUin(), fromGroupUserUserName);
                weChatMessage.setContent(rawContent.substring(colonIndex + 6));
                weChatMessage.setFromGroupUserName(fromGroup.getUsername());
                weChatMessage.setFromGroup(fromGroup.getNickname());
            }
        }
        weChatMessage.setToUserName(toUser.getUsername());
        weChatMessage.setToUser(toUser.getNickname());
        weChatMessage.setFromUserName(fromUser.getUsername());
        weChatMessage.setFromUser(fromUser.getNickname());
        if (isSend) {
            weChatMessage.setDirection(WeChatConstants.MessageDirection.SEND);
            weChatMessage.setRemarkName(toUser.getRemarkName());
        } else {
            weChatMessage.setDirection(WeChatConstants.MessageDirection.RECEIVE);
            weChatMessage.setHeadImg(fromUser.getIcon());
            weChatMessage.setRemarkName(fromUser.getRemarkName());
        }
        return weChatMessage;

    }

    void setBasicInfo(MessageResponse response, WeChatMessage weChatMessage) {
        weChatMessage.setMsgId(response.getMsgId());
        weChatMessage.setAppMsgType(response.getAppMsgType());
        weChatMessage.setMsgType(response.getMsgType());
        weChatMessage.setSubMsgType(response.getSubMsgType());
        weChatMessage.setCreateTime(response.getCreateTime() * 1000);
        weChatMessage.setMediaId(response.getMediaId());
    }

    protected void log(WeChatContext context, WeChatMessage message) {
        if (message.isGroupMessage() && message.getDirection() == WeChatConstants.MessageDirection.RECEIVE) {
            log.info("[*] |{}|{}|:{}群{}消息 => {}: {}|{} -> {}", context.getId(), context.getUuid(), message.getDirection().getDesc(), getSupportedType().getName(), message.getFromGroup(), message.getFromUser(), message.getToUser());
        } else {
            log.info("[*] |{}|{}|:{}{}消息 => {} -> {}", context.getId(), context.getUuid(), message.getDirection().getDesc(), getSupportedType().getName(), message.getFromUser(), message.getToUser());
        }
    }


    String match(Pattern pattern, String target) {
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

}
