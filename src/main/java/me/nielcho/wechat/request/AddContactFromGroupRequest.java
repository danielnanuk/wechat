package me.nielcho.wechat.request;

import lombok.Data;

@Data
public class AddContactFromGroupRequest {
    String username;
    String content;
}
