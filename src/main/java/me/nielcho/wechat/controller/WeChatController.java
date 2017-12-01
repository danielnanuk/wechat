package me.nielcho.wechat.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.manager.WeChatManager;
import me.nielcho.wechat.request.AddContactFromGroupRequest;
import me.nielcho.wechat.request.SendMsgRequest;
import me.nielcho.wechat.request.SetRemarkRequest;
import me.nielcho.wechat.response.*;
import me.nielcho.wechat.service.WeChatService;
import me.nielcho.wechat.session.WeChatSession;
import okhttp3.Response;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Api("微信")
@RestController
@RequestMapping("/apis")
public class WeChatController {
    @Autowired
    WeChatManager weChatManager;

    @Autowired
    WeChatService weChatService;

    @ApiOperation(value = "获得登录用二维码", produces = "image/png")
    @RequestMapping(value = "/createSession", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void createSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionId = request.getSession().getId();
        WeChatSession session = weChatManager.createSession(sessionId);
        String url = WeChatConstants.WX_LOGIN_QRCODE + session.getUUID();
        response.setContentType("image/png");
        writeToStream(url, response.getOutputStream());
    }

    @ApiOperation("获取当前登录的微信用户")
    @RequestMapping(value = "/wechat_user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getWeChatUser(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        WeChatSession session = weChatManager.getSession(sessionId);
        WeChatContext context = session.getContext();
        return context.getUser();
    }

    @ApiOperation("获得所有联系人")
    @RequestMapping(value = "/contacts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ContactInfo> getContacts() {
        WeChatContext context = weChatManager.getContext();
        return weChatService.getAllContacts(context);
    }

    @ApiOperation("根据UserName获取联系人信息")
    @RequestMapping(value = "/getContactInfo", method = RequestMethod.GET)
    @ResponseBody
    public ContactInfo getContactInfo(@RequestParam(name = "username") String username) {
        WeChatContext context = weChatManager.getContext();
        return weChatService.getContactInfo(context, username, true);
    }

    @ApiOperation("设置备注名")
    @RequestMapping(value = "/setRemarkName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SetRemarkResponse setRemarkName(@RequestBody SetRemarkRequest setRemarkRequest) {
        WeChatContext context = weChatManager.getContext();
        String to = setRemarkRequest.getUserName();
        String remarkName = setRemarkRequest.getRemarkName();
        return weChatService.setRemarkName(context, to, remarkName);
    }

    @ApiOperation("发送文本消息")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public SendMessageResponse send(@RequestBody SendMsgRequest request) {
        request.validate();
        WeChatContext context = weChatManager.getContext();
        return weChatService.sendTextMessage(context, request.getTo(), request.getContent());
    }

    @ApiOperation("发送图片/文件")
    @RequestMapping(value = "/sendFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SendMessageResponse sendFile(MultipartFile file, @RequestParam(name = "to") String to) throws IOException {
        WeChatContext context = weChatManager.getContext();
        return weChatService.sendMedia(context, file, to);
    }

    @ApiOperation("登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean logout() {
        WeChatContext context = weChatManager.getContext();
        if (context != null && context.getDomain() != null && context.getDomain().length() > 0) {
            weChatService.logout(context);
        }
        return true;
    }

    @ApiOperation("获得头像/群头像")
    @RequestMapping(value = "/getIcon", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void getIcon(HttpServletResponse response, @RequestParam("username") String username) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getIcon(context, username);
        copy(response, weChatResponse);
    }

    @ApiOperation("获得群里成员头像")
    @RequestMapping(value = "/getGroupMemberIcon", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void getIcon(HttpServletResponse response, @RequestParam("username") String username, @RequestParam(required = false) String chatroomid) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getGroupIcon(context, username, chatroomid);
        copy(response, weChatResponse);
    }

    @ApiOperation("获得消息图片/视频图片, type传slave或者full")
    @RequestMapping(value = "/images/{msgId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void getMsgImage(HttpServletResponse response, @PathVariable("msgId") String msgId, @RequestParam(defaultValue = "", name = "type") String type) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getMsgImage(context, msgId, type);
        copy(response, weChatResponse);
    }

    @ApiOperation("获得语音消息")
    @RequestMapping(value = "/voices/{msgId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getVoice(HttpServletResponse response, @PathVariable("msgId") String msgId) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getVoice(context, msgId);
        copy(response, weChatResponse);
    }

    @ApiOperation("获得视频消息")
    @RequestMapping(value = "/videos/{msgId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getVideo(HttpServletResponse response, @PathVariable("msgId") String msgId) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getVideo(context, msgId);
        copy(response, weChatResponse);
    }

    @ApiOperation("添加群聊好友/名片好友")
    @RequestMapping(value = "/addContact", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addContact(@RequestBody AddContactFromGroupRequest addContactRequest) {
        WeChatContext context = weChatManager.getContext();
        String username = addContactRequest.getUsername();
        String content = addContactRequest.getContent();
        return weChatService.addContact(context, username, content);
    }

    @ApiOperation("下载文件")
    @RequestMapping(value = "/downloadmedia", method = RequestMethod.GET)
    public void downloadMedia(@RequestParam String sender, @RequestParam String mediaId, @RequestParam String filename, HttpServletResponse response) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getMedia(context, sender, mediaId, filename);
        copy(response, weChatResponse);
    }

    @ApiOperation("位置图片")
    @RequestMapping(value = "/locationImage", method = RequestMethod.GET)
    public void getLocationImage(@RequestParam String url, @RequestParam String msgId, HttpServletResponse response) throws IOException {
        WeChatContext context = weChatManager.getContext();
        Response weChatResponse = weChatService.getLocationImage(context, url, msgId);
        copy(response, weChatResponse);
    }

    private void copy(HttpServletResponse response, Response weChatResponse) throws IOException {
        response.setContentType(weChatResponse.body().contentType().toString());
        weChatResponse.headers().toMultimap().forEach((k, v) -> response.setHeader(k, StringUtils.join(v, ';')));
        response.setDateHeader("Expires", System.currentTimeMillis() + 3600 * 1000 * 24 * 7);
        response.setHeader("Cache-Control", "max-age=604800");
        response.setHeader("Pragma", "Public");
        int contentLength = StreamUtils.copy(weChatResponse.body().byteStream(), response.getOutputStream());
        response.setContentLength(contentLength);
    }

    private void writeToStream(String content, OutputStream out) {
        int height = 300;
        int width = 300;
        String format = "png";
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_SQUARE);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, format, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
