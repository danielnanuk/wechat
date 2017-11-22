package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseWxResponse {
    @JsonProperty("BaseResponse")
    private BaseResponse BaseResponse;
    
    public boolean isSuccess() {
        return BaseResponse != null && BaseResponse.getRet() == 0;
    }
    
    public static boolean isSuccess(BaseWxResponse baseWxResponse) {
        return baseWxResponse != null && baseWxResponse.isSuccess();
    }
}
