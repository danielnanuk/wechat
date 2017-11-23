package me.nielcho.wechat.request;


import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
public class SendMsgRequest {
    String to;
    List<String> receivers;
    String content;
    
    
    
    public void validate() {
        if (StringUtils.isEmpty(to) && CollectionUtils.isEmpty(receivers)) {
            throw new IllegalArgumentException("接收人为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("消息为空");
        }
        if (content.length() > 5000) {
            throw new IllegalArgumentException("发送内容过长, 最大5000个汉字!");
        }
    }
}
