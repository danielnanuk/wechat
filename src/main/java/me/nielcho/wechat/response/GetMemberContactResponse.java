package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class GetMemberContactResponse extends BaseWxResponse {
    @JsonProperty("MemberList")
    List<GetContactResponse> MemberList;
    @JsonProperty("MemberCount")
    int MemberCount;
    @JsonProperty("Seq")
    long seq;
}
