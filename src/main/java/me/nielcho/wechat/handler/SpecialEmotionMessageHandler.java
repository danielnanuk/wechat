package me.nielcho.wechat.handler;

import me.nielcho.wechat.constants.WeChatConstants;
import org.springframework.stereotype.Component;

@Component
public class SpecialEmotionMessageHandler extends ImageMessageHandler {
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.SPECIAL_EMOTION;
    }
    
}
