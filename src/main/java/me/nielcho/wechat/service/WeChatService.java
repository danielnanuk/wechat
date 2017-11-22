package me.nielcho.wechat.service;

import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import org.springframework.stereotype.Service;

@Service
public class WeChatService {
    public ContactInfo getContactInfo(WeChatContext context, String toUserName, boolean remote) {
        return null;
    }
}
