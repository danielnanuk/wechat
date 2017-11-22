package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ShareLinkMessageHandler extends MessageHandler {
    
    private static final Pattern DESC_PATTERN = Pattern.compile("<des>(.+?)</des>");
    private static final Pattern THUMB_URL_PATTERN = Pattern.compile("<thumburl>(.+?)</thumburl>");
    
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.SHARE_LINK;
    }
    
    @Override
    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {
        weChatMessage.setTitle(message.getFileName());
        weChatMessage.setUrl(message.getUrl());
        String rawContent = message.getContent();
        String parsed = parseXml(rawContent);
        Matcher descMatcher = DESC_PATTERN.matcher(parsed);
        if (descMatcher.find()) {
            String desc = descMatcher.group(1);
            weChatMessage.setContent(desc);
        }
        Matcher thumbMatcher = THUMB_URL_PATTERN.matcher(parsed);
        if (thumbMatcher.find()) {
            weChatMessage.setThumbnail(thumbMatcher.group(1));
        }
    }
}
