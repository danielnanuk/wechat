package me.nielcho.wechat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.WeChatMessage;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CardMessageHandler extends MessageHandler {

    private static final Pattern HEAD_IMG_PATTERN = Pattern.compile("smallheadimgurl=\"(\\S+?)\"");
    private static final Pattern WX_NO_PATTERN = Pattern.compile("username=\"(\\S+?)\"");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("nickname=\"(\\S+?)\"");
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.CARD;
    }
    
    @Override
    public void handleInternal(WeChatContext context, MessageResponse message, WeChatMessage weChatMessage) {
        JSONObject recommendInfo = JSONObject.parseObject(message.getRecommendInfo());
        String username = recommendInfo.getString("UserName");
        String parsed = parseXml(weChatMessage.getContent());
        String headImg = match(HEAD_IMG_PATTERN, parsed);
        String wxNo = match(WX_NO_PATTERN, parsed);
        String nickname = recommendInfo.getString("NickName");
        Map<String, String> content = new HashMap<>(4);
        content.put("username", username);
        content.put("icon", headImg);
        content.put("wxNo", wxNo);
        content.put("nickname", nickname);
        weChatMessage.setContent(JSON.toJSONString(content));
        log.info("");
    }
    
    
}
