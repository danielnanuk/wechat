package me.nielcho.wechat.controller;

import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.manager.WeChatManager;
import me.nielcho.wechat.session.WeChatSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat")
public class WeChatController {
    @Autowired
    WeChatManager weChatManager;

    @RequestMapping(value = "/createSession", method = RequestMethod.GET)
    public String createSession(@RequestParam String id) {
        WeChatSession session = weChatManager.createSession(id);
        return WeChatConstants.WX_LOGIN_QRCODE + session.getUUID();
    }


}
