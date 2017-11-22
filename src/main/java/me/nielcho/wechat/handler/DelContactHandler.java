package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.response.DelContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@Slf4j
public class DelContactHandler implements BiConsumer<WeChatContext, DelContact> {


    @Autowired
    private ContactRepository contactRepository;

    @Override
    public void accept(WeChatContext context, DelContact delContact) {
        ContactInfo contactInfo = contactRepository.getContact(context.getUin(), delContact.getUserName());
        log.info("[x] |{}| 收到删除联系人信息: delContact: {}, contactInfo: {}", context.getUin(), delContact, contactInfo);
    }
}
