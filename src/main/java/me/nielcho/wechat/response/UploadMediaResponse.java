package me.nielcho.wechat.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UploadMediaResponse extends BaseWxResponse {
 
    @JSONField(name = "MediaId")
    String MediaId;
    @JSONField(name = "StartPos")
    int StartPos;
    @JSONField(name = "CDNThumbImgHeight")
    int CDNThumbImgHeight;
    @JSONField(name = "CDNThumbImgWidth")
    int CDNThumbImgWidth;
}
