package me.nielcho.wechat.handler;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.event.ModContactEvent;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.response.ModContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;


@Component
@Slf4j
public class ModContactHandler implements BiConsumer<WeChatContext, ModContact> {
    
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Override
    public void accept(WeChatContext context, ModContact modContact) {
        ContactInfo current = ContactInfo.fromModContact(context, modContact);
        String username = current.getUsername();
        log.info("[x] |{}| 收到联系人变更信息:{}", context.getUin(), current);
        ContactInfo previous = contactRepository.getContact(context.getUin(), username);
        eventPublisher.publishEvent(ModContactEvent.builder()
                .source(this)
                .context(context)
                .previous(previous)
                .modContact(modContact)
                .current(current).build());
    }
    
}
