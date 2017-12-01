package me.nielcho.wechat.repository;

import me.nielcho.wechat.domain.ContactInfo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContactRepository {
    private Map<String, Map<String, ContactInfo>> contactMap = new HashMap<>();

    public void addContacts(String uin, Collection<ContactInfo> contacts) {
        Map<String, ContactInfo> contactInfoMap = contactMap.computeIfAbsent(uin, (key) -> new HashMap<>());
        contacts.forEach(contact -> contactInfoMap.put(contact.getUsername(), contact));
    }

    public ContactInfo getContact(String uin, String username) {
        if (contactMap.containsKey(uin)) {
            return contactMap.get(uin).get(username);
        }
        return null;
    }

    public List<ContactInfo> getAllContact(String uin) {
        return new ArrayList<>(contactMap.get(uin).values());
    }
}
