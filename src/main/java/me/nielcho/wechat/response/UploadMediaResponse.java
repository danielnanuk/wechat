package me.nielcho.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class UploadMediaResponse extends BaseWxResponse {
 
    @JsonProperty("MediaId")
    String MediaId;
    @JsonProperty("StartPos")
    int StartPos;
    @JsonProperty("CDNThumbImgHeight")
    int CDNThumbImgHeight;
    @JsonProperty("CDNThumbImgWidth")
    int CDNThumbImgWidth;
}
