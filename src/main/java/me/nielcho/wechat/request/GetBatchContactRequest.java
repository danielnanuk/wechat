package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class GetBatchContactRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "Count")
    int Count;
    @JSONField(name = "List")
    List<GetBatchUserName> List;
}
