package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitMessageHandler extends MessageHandler {
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Override
    public WeChatConstants.MessageType getSupportedType() {
        return WeChatConstants.MessageType.INIT;
    }
    
    @Override
    public void handle(WeChatContext context, MessageResponse message) {
        switch (message.getStatusNotifyCode()) {
            case 2:
                //进入聊天窗口
                String username = message.getToUserName();
                log.info("[x] |{}| {} for {}", context.getUin(), getSupportedType().getName(), username);
                break;
            case 5:
                //离开聊天窗口, 暂时不处理
                break;
            
            
        }
    }
}
