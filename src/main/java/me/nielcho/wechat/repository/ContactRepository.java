package me.nielcho.wechat.repository;

import me.nielcho.wechat.domain.ContactInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContactRepository {
    Map<String, Map<String, ContactInfo>> contactMap = new HashMap<>();

    public void addContacts(String uin, Collection<ContactInfo> contacts) {
        contactMap.computeIfAbsent(uin, (key) -> {
            Map<String, ContactInfo> contactInfoMap = new HashMap<>();
            contacts.forEach(contact -> contactInfoMap.put(contact.getUsername(), contact));
            return contactInfoMap;
        });
    }

    public ContactInfo getContact(String uin, String username) {
        if (contactMap.containsKey(uin)) {
            return contactMap.get(uin).get(username);
        }
        return null;
    }


}
