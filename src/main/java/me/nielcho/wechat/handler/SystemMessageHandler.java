package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class SystemMessageHandler extends MessageHandler {
    
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.SYSTEM;
    }
    
    @Override
    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {
    }
    
    @Override
    WeChatMessage fromMessageResponse(WeChatContext context, MessageResponse response) {
        WeChatMessage weChatMessage = new WeChatMessage();
        setBasicInfo(response, weChatMessage);
        ContactInfo fromUser = contactRepository.getContact(context.getUin(), response.getFromUserName());
        if (fromUser == null) {
            log.info("[*] |{}|{}|:未知系统消息:{}", context.getId(), context.getUuid(), response);
            return null;
        }
        ContactInfo toUser = contactRepository.getContact(context.getUin(), response.getToUserName());
        if (toUser == null) {
            log.info("[*] |{}|{}|:未知系统消息:{}", context.getId(), context.getUuid(), response);
            return null;
        }
        boolean groupMessage = fromUser.isGroup() || toUser.isGroup();
        boolean isSend = Objects.equals(fromUser.getUsername(), context.getUser().getUserName());
        weChatMessage.setGroupMessage(groupMessage);
        weChatMessage.setContent(response.getContent());
        if (fromUser.isGroup()) {
            weChatMessage.setFromGroup(fromUser.getNickname());
            weChatMessage.setFromGroupUserName(fromUser.getUsername());
        } else {
            weChatMessage.setFromUser(fromUser.getNickname());
            weChatMessage.setFromUserName(fromUser.getUsername());
        }
        weChatMessage.setToUser(toUser.getNickname());
        weChatMessage.setToUserName(toUser.getUsername());
        if (isSend) {
            weChatMessage.setDirection(WeChatConstants.MessageDirection.SEND);
        } else {
            weChatMessage.setDirection(WeChatConstants.MessageDirection.RECEIVE);
            weChatMessage.setHeadImg(fromUser.getIcon());
        }
        return weChatMessage;
    }
}
