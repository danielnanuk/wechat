package me.nielcho.wechat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadMediaRequest {
    @JsonProperty("BaseRequest")
    BaseRequest BaseRequest;
    @JsonProperty("ClientMediaId")
    long ClientMediaId;
    @JsonProperty("TotalLen")
    long TotalLen;
    @JsonProperty("StartPos")
    int StartPos;
    @JsonProperty("DataLen")
    long DataLen;
    @JsonProperty("MediaType")
    int MediaType;
    @JsonProperty("UploadType")
    int UploadType;
    @JsonProperty("FileMd5")
    String FileMd5;
}
