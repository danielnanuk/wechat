package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class BaseWxResponse {
    @JSONField(name = "BaseResponse")
    private BaseResponse BaseResponse;
    
    private boolean isSuccess() {
        return BaseResponse != null && BaseResponse.getRet() == 0;
    }
    
    public static boolean isSuccess(BaseWxResponse baseWxResponse) {
        return baseWxResponse != null && baseWxResponse.isSuccess();
    }
}
