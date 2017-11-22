package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncKey {
    @JsonProperty("Count")
    int Count;
    @JsonProperty("List")
    List<SyncKeyPair> List;
}
