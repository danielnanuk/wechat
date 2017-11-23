package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SyncKey {
    @JSONField(name = "Count")
    int Count;
    @JSONField(name = "List")
    List<SyncKeyPair> List;
}
