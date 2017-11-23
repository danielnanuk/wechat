package me.nielcho.wechat.handler;


import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LocationMessageHandler extends MessageHandler {
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.LOCATION;
    }
    
    @Override
    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {
        String rawContent = weChatMessage.getContent();
        String[] arr = rawContent.split(":<br/>");
        weChatMessage.setTitle(arr[0]);
        weChatMessage.setThumbnail(arr[1]);
        weChatMessage.setUrl(message.getUrl());
        weChatMessage.getDirection();

    }
}
