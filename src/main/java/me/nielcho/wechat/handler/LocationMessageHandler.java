package me.nielcho.wechat.handler;


import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.response.MessageResponse;

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

    }
}
