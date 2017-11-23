package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SyncKeyPair {
    @JSONField(name = "Key")
    int Key;
    @JSONField(name = "Val")
    int Val;
}
