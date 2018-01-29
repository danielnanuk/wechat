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
public class EmotionMessageHandler extends MessageHandler {

    private static final Pattern CDN_URL_PATTERN = Pattern.compile("cdnurl\\s*=\\s*\"(\\S+?)\"");

    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.EMOTION;
    }

    @Override
    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {
        Matcher matcher = CDN_URL_PATTERN.matcher(weChatMessage.getContent());
        if (matcher.find()) {
            weChatMessage.setUrl(matcher.group(1));
            weChatMessage.setContent("");
        } else {
            weChatMessage.setContent("收到表情包表情, 请在手机上查看");
        }
    }
}
