package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncKeyPair {
    @JsonProperty("Key")
    int Key;
    @JsonProperty("Val")
    int Val;
}
