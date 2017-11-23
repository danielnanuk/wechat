package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class AddContactRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "Opcode")
    int Opcode;
    @JSONField(name = "SceneList")
    List<Integer> SceneList;
    @JSONField(name = "SceneListCount")
    int SceneListCount;
    @JSONField(name =  "VerifyContent")
    String VerifyContent;
    @JSONField(name = "VerifyUserListSize")
    int VerifyUserListSize;
    @JSONField(name = "VerifyUserList")
    List<VerifyUserRequest> VerifyUserList;
    @JSONField(name = "skey")
    String skey;
}
