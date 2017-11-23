package me.nielcho.wechat.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class UploadMediaRequest {
    @JSONField(name = "BaseRequest")
    BaseRequest BaseRequest;
    @JSONField(name = "ClientMediaId")
    long ClientMediaId;
    @JSONField(name = "TotalLen")
    long TotalLen;
    @JSONField(name = "StartPos")
    int StartPos;
    @JSONField(name = "DataLen")
    long DataLen;
    @JSONField(name = "MediaType")
    int MediaType;
    @JSONField(name = "UploadType")
    int UploadType;
    @JSONField(name = "FileMd5")
    String FileMd5;
}
