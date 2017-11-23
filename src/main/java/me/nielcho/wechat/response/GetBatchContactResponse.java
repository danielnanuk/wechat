package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(callSuper = true)
public class GetBatchContactResponse extends BaseWxResponse {
    @JSONField(name = "ContactList")
    List<GetContactResponse> ContactList;
    @JSONField(name = "Count")
    int Count;
    
}
