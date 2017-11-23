package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class GetMemberContactResponse extends BaseWxResponse {
    @JSONField(name = "MemberList")
    List<GetContactResponse> MemberList;
    @JSONField(name = "MemberCount")
    int MemberCount;
    @JSONField(name = "Seq")
    long seq;
}
