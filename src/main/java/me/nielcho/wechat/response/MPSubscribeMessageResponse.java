package me.nielcho.wechat.response;

import lombok.Data;

import java.util.List;

@Data
public class MPSubscribeMessageResponse {
    int MPArticleCount;
    List<MPArticleResponse> MPArticleList;
    String NickName;
    Long Time;
    String UserName;
}
