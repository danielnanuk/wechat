package me.nielcho.wechat.repository;

import me.nielcho.wechat.domain.ContactInfo;

import java.util.Collection;
import java.util.List;

public interface ContactRepository {

    void addContacts(String uin, Collection<ContactInfo> contacts);

    List<ContactInfo> getAllContacts(String uin, boolean includeGroupMember);

    ContactInfo getContact(String uin, String userName);

    void clearContact(String uin);


}
