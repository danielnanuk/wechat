package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(callSuper = true)
public class GetBatchContactResponse extends BaseWxResponse {
    @JsonProperty("ContactList")
    List<GetContactResponse> ContactList;
    @JsonProperty("Count")
    int Count;
    
}
