package me.nielcho.wechat.request;

import lombok.Data;

import java.util.List;

@Data
public class SearchActorIdRequest {
    List<Item> items;
    Integer employeeId;

    @Data
    public static class Item {
        String username;
        List<String> names;
        List<String> numbers;
    }
}
