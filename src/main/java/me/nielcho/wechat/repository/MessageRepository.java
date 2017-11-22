package me.nielcho.wechat.repository;


import me.nielcho.wechat.domain.WeChatMessage;

import java.util.List;

public interface MessageRepository {

    void addMessage(String uin, String username, WeChatMessage message);

    List<WeChatMessage> getMessages(String uin, String username);
}
