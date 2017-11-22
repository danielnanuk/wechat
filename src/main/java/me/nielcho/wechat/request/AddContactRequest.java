package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddContactRequest {
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
    @JsonProperty("Opcode")
    int Opcode;
    @JsonProperty("SceneList")
    List<Integer> SceneList;
    @JsonProperty("SceneListCount")
    int SceneListCount;
    @JsonProperty( "VerifyContent")
    String VerifyContent;
    @JsonProperty("VerifyUserListSize")
    int VerifyUserListSize;
    @JsonProperty("VerifyUserList")
    List<VerifyUserRequest> VerifyUserList;
    @JsonProperty("skey")
    String skey;
}
