package me.nielcho.wechat.repository;

import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.util.Util;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContactRepository {
    private Map<String, Map<String, ContactInfo>> contactMap = new HashMap<>();

    public void addContacts(String uin, Collection<ContactInfo> contacts) {
        contactMap.computeIfAbsent(uin, (key) -> new HashMap<>())
                .putAll(contacts.stream()
                        .collect(Collectors.toMap(ContactInfo::getUsername, Function.identity())));
    }

    public ContactInfo getContact(String uin, String username) {
        return Util.map(contactMap.get(uin), m -> m.get(username));
    }

    public List<ContactInfo> getAllContact(String uin) {
        return new ArrayList<>(contactMap.get(uin).values());
    }
}
