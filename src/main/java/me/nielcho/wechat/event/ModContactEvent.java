package me.nielcho.wechat.event;


import lombok.Data;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.response.ModContact;
import org.springframework.context.ApplicationEvent;

@Data
public class ModContactEvent extends ApplicationEvent {
    private ContactInfo previous;
    private ContactInfo current;
    private WeChatContext context;
    private ModContact modContact;

    ModContactEvent(Object source, WeChatContext context, ContactInfo previous, ContactInfo current, ModContact modContact) {
        super(source);
        this.context = context;
        this.previous = previous;
        this.current = current;
        this.modContact = modContact;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContactInfo previous;
        private ContactInfo current;
        private Object source;
        private WeChatContext context;
        private ModContact modContact;

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder previous(ContactInfo previous) {
            this.previous = previous;
            return this;
        }

        public Builder current(ContactInfo current) {
            this.current = current;
            return this;
        }

        public Builder context(WeChatContext context) {
            this.context = context;
            return this;
        }
        public Builder modContact(ModContact modContact) {
            this.modContact = modContact;
            return this;
        }

        public ModContactEvent build() {
            return new ModContactEvent(source, context, previous, current, modContact);
        }

    }
}
