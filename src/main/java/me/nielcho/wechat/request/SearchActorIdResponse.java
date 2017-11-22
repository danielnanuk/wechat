package me.nielcho.wechat.request;

import lombok.Data;

@Data
public class SearchActorIdResponse {
    private Integer code;
    private String message;
    private String data;
}
