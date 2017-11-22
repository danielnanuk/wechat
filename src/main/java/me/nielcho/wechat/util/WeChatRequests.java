package me.nielcho.wechat.util;

import lombok.Data;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.predicate.ContactPredicate;
import me.nielcho.wechat.request.*;
import okhttp3.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static me.nielcho.wechat.constants.WeChatConstants.LANG;
import static me.nielcho.wechat.constants.WeChatConstants.WX_GET_CONTACT_URL;

public class WeChatRequests {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Data
    public static class Tag {
        private Integer employeeId;
        private String description;
        private Tag(Integer employeeId, String description) {
            this.employeeId = employeeId;
            this.description = description;
        }
        public void log(Logger logger, Response response) {
            logger.info("[x] {}: {} ms for {} -> {}:{}", employeeId, System.currentTimeMillis() - response.sentRequestAtMillis(), description, response.code(), response.message());
        }
    }

    private static Tag tag(WeChatContext context, String description) {
        return new Tag(context.getEmployeeId(), description);
    }

    private static Request.Builder requestBuilderForWeChat(WeChatContext context) {
        Request.Builder builder = new Request.Builder();
        builder.header("User-agent", context.getUserAgent());
        if (!StringUtils.isEmpty(context.getDomain())) {
            builder.header("Referer", context.getDomain());
        }
        if (!StringUtils.isEmpty(context.getHost())) {
            builder.addHeader("Host", context.getHost());
        }
        if (!StringUtils.isEmpty(context.getDomain())) {
            builder.header("Origin", context.getDomain());
        }
        if (MapUtils.isNotEmpty(context.getCookies())) {
            Map<String, String> cookies = context.getCookies();
            String setCookies = cookies.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue() + ";").collect(Collectors.joining());
            builder.addHeader("Cookie", setCookies);
        }
        return builder;
    }

    private static RequestBody generateJSONBody(Object object) {
        return RequestBody.create(JSON_TYPE, (object instanceof String) ? (String) object : JsonUtil.toJsonString(object));
    }

    public static Request startLoginRequest(WeChatContext context) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", WeChatConstants.WEB_WX_APPID);
        params.put("fun", "new");
        params.put("lang", LANG);
        params.put("_", System.currentTimeMillis());
        params.put("redirect_uri", WeChatConstants.DEFAULT_REDIRECT_URI);
        String url = URLUtils.format(WeChatConstants.WX_LOGIN_REQUEST_URL, params);
        return requestBuilderForWeChat(context).url(url).tag(tag(context, "开始登录")).build();
    }

    public static Request waitForLoginRequest(WeChatContext context) {
        Map<String, Object> params = new HashMap<>();
        params.put("loginicon", "false");
        params.put("tip", context.getTip());
        params.put("uuid", context.getUuid());
        params.put("_", System.currentTimeMillis());
        params.put("r", WeChatUtil.invertLong(System.currentTimeMillis()));
        String url = URLUtils.format(WeChatConstants.WX_CHECK_LOGIN_URL, params);
        return requestBuilderForWeChat(context).url(url).tag(tag(context, "等待登录")).build();
    }

    public static Request openStatusNotifyRequest(WeChatContext context) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_OPEN_STATUS_NOTIFY_URL, LANG, context.getPassTicket());
        String userName = context.getUser().getUserName();
        OpenStatusNotifyRequest request = new OpenStatusNotifyRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setCode(WeChatConstants.WX_OPEN_STATUS_NOTIFY_CODE);
        request.setFromUserName(userName);
        request.setToUserName(userName);
        request.setClientMsgId(System.currentTimeMillis());
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "开启状态通知")).build();
    }

    public static Request doRedirectLoginRequest(WeChatContext context, String redirectUrl) {
        return requestBuilderForWeChat(context).url(redirectUrl + "&fun=new&version=v2").tag(tag(context, "登录重定向")).build();
    }

    public static Request initWeChatRequest(WeChatContext context) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_INIT_URL, System.currentTimeMillis() / 1000, LANG, context.getPassTicket(), context.getSkey());
        WeChatInitRequest request = new WeChatInitRequest();
        request.setBaseRequest(context.getBaseRequest());
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "初始化微信")).build();
    }

    public static Request getContactRequest(WeChatContext context, long seq) {
        return requestBuilderForWeChat(context).url(URLUtils.format(context.getDomain() + WX_GET_CONTACT_URL, LANG, System.currentTimeMillis(), seq, context.getSkey())).tag(tag(context, "获取微信联系人")).build();
    }

    public static Request getBatchContactRequest(WeChatContext context, Collection<String> usernames) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_GET_BATCH_CONTACT_URL, System.currentTimeMillis(), LANG);
        GetBatchContactRequest request = new GetBatchContactRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setCount(usernames.size());
        request.setList(usernames.stream().map(GetBatchUserName::fromUserName).collect(Collectors.toList()));
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context,"批量获取微信联系人")).build();
    }

    public static Request wxSyncRequest(WeChatContext context) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_SYNC_URL, context.getSid(), context.getSkey(), context.getPassTicket(), LANG);
        WeChatSyncRequest request = new WeChatSyncRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setRr(WeChatUtil.invertLong(System.currentTimeMillis()));
        request.setSyncKey(context.getSyncKey());
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context,"获取新消息")).build();
    }

    public static Request syncCheckRequest(WeChatContext context) {
        String syncKeys = context.getSyncKey().getList().stream().map(k -> k.getKey() + "_" + k.getVal()).collect(Collectors.joining("|"));
        String url = URLUtils.format(context.getPushDomain() + WeChatConstants.WX_SYNC_CHECK_URL, System.currentTimeMillis(), context.getSkey(), context.getSid(), context.getUin(), context.getDeviceId(), syncKeys, System.currentTimeMillis());
        String domain = context.getPushDomain();
        return requestBuilderForWeChat(context).url(url).removeHeader("Origin").header("Host", getHost(domain)).tag(tag(context,"SyncCheck检查")).build();
    }

    public static Request getVoiceRequest(WeChatContext context, String msgId) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_GET_VOICE_URL, msgId, context.getSkey());
        return requestBuilderForWeChat(context).url(url).tag(tag(context,"获取语音消息内容")).build();
    }

    public static Request getImageRequest(WeChatContext context, String msgId, String type) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_GET_IMAGE_URL, msgId, context.getSkey()) + ("slave".equalsIgnoreCase(type) ? "&type=slave" : "");
        return requestBuilderForWeChat(context).url(url).tag(tag(context,"获取图片内容")).build();
    }

    public static Request setRemarkRequest(WeChatContext context, String to, String remarkName) {
        String url = context.getDomain() + WeChatConstants.WX_SET_REMARK_URL;
        SetRemarkRequest request = SetRemarkRequest.newSetRemarkRequest(context, to, remarkName);
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context,"设置备注")).build();
    }

    public static Request logoutRequest(WeChatContext context) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_LOGOUT_URL, context.getSkey());
        Map<String, String> params = new HashMap<>();
        params.put("sid", context.getSid());
        params.put("uin", context.getUin());
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(params)).tag(tag(context,"登出")).build();
    }

    public static Request sendImageRequest(WeChatContext context, String to, String mediaId) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_SEND_IMAGE_URL, context.getPassTicket());
        SendMediaRequest request = new SendMediaRequest();
        request.setBaseRequest(context.getBaseRequest());
        Msg msg = Msg.newImageMsg(context, to, mediaId);
        request.setMsg(msg);
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context,"发送图片")).build();
    }

    public static Request sendFileRequest(WeChatContext context, MultipartFile file, String to, String mediaId) {
        String url = context.getDomain() + WeChatConstants.WX_SEND_FILE_URL;
        String filename = file.getOriginalFilename();
        String content = String.format(
                WeChatConstants.WX_SEND_FILE_TEMPLATE,
                WeChatConstants.SEND_FILE_APPID,
                filename,
                file.getSize(),
                mediaId,
                WeChatUtil.getFileExt(filename)
        );
        SendMediaRequest request = new SendMediaRequest();
        request.setBaseRequest(context.getBaseRequest());
        Msg msg = Msg.newFileMsg(context, to, mediaId);
        msg.setContent(content);
        request.setMsg(msg);
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context,"发送文件")).build();
    }

    public static Request getIconRequest(WeChatContext context, String username, String chatRoomId) {
        String requestUrl = context.getDomain() + (ContactPredicate.isGroupContact(username) ? WeChatConstants.WX_GET_GROUP_ICON : WeChatConstants.WX_GET_ICON);
        Map<String, Object> params = new HashMap<>();
        params.put("seq", 0);
        params.put("username", username);
        params.put("skey", context.getSkey());
        if (!StringUtils.isEmpty(chatRoomId)) {
            params.put("chatroomid", chatRoomId);
        }
        String url = URLUtils.format(requestUrl, params);
        return requestBuilderForWeChat(context).url(url).tag(tag(context,"获得头像")).build();
    }

    public static Request getMediaRequest(WeChatContext context, String sender, String mediaId, String filename) {
        Map<String, Object> params = new HashMap<>();
        params.put("sender", sender);
        params.put("mediaid", mediaId);
        params.put("filename", filename);
        params.put("fromuser", context.getUin());
        params.put("pass_ticket", context.getPassTicket());
        params.put("webwx_data_ticket", context.getCookies().get("webwx_data_ticket"));
        String url = URLUtils.format(context.getFileDomain() + WeChatConstants.WX_GET_MEDIA_URL, params);
        return requestBuilderForWeChat(context).url(url).header("Host", getHost(context.getFileDomain())).tag(tag(context,"下载多媒体文件")).build();
    }

    public static Request getLocationImage(WeChatContext context, String locationUrl, String msgId) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_GET_LOCATION_IMAGE_URL, locationUrl, msgId);
        return requestBuilderForWeChat(context).url(url).tag(tag(context, "获取地址图片")).build();
    }

    public static Request getVideoRequest(WeChatContext context, String msgId) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_GET_VIDEO_URL, msgId, context.getSkey());
        return requestBuilderForWeChat(context).url(url).header("Range", "bytes=0-").tag(tag(context, "获取视频内容")).build();
    }

    public static Request sendMediaRequest(WeChatContext context, MultipartFile file, String contentType, String mediaType, Integer mediaCount) throws IOException {
        String url = context.getFileDomain() + WeChatConstants.WX_UPLOAD_MEDIA_URL;
        RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file.getBytes());
        UploadMediaRequest requestPayload = new UploadMediaRequest();
        requestPayload.setBaseRequest(context.getBaseRequest());
        requestPayload.setClientMediaId(WeChatUtil.generateClientMediaId());
        requestPayload.setDataLen(file.getSize());
        requestPayload.setTotalLen(file.getSize());
        requestPayload.setStartPos(0);
        requestPayload.setUploadType(2);
        requestPayload.setMediaType(4);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", "WU_FILE_" + mediaCount)
                .addFormDataPart("name", file.getOriginalFilename())
                .addFormDataPart("type", contentType)
                .addFormDataPart("size", String.valueOf(file.getSize()))
                .addFormDataPart("lastModifiedDate", new Date().toString())
                .addFormDataPart("mediatype", mediaType)
                .addFormDataPart("uploadmediarequest", JsonUtil.toJsonString(requestPayload))
                .addFormDataPart("webwx_data_ticket", context.getCookies().get("webwx_data_ticket"))
                .addFormDataPart("pass_ticket", context.getPassTicket())
                .addFormDataPart("filename", file.getOriginalFilename(), fileBody).build();
        return requestBuilderForWeChat(context).url(url).post(requestBody).header("Host", getHost(context.getFileDomain())).tag(tag(context, "上传多媒体文件")).build();
    }

    private static String getHost(String domain) {
        return domain.substring(domain.lastIndexOf("/") + 1);
    }

    public static Request sendTextRequest(WeChatContext context, String to, String content) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_SEND_TEXT_URL, context.getPassTicket());
        Msg msg = Msg.newTextMsg(context, to, content);
        SendTextRequest request = new SendTextRequest();
        request.setMsg(msg);
        request.setBaseRequest(context.getBaseRequest());
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "发送文本消息")).build();
    }

    public static Request addContactRequest(WeChatContext context, String username, String content) {
        String url = URLUtils.format(context.getDomain() + WeChatConstants.WX_ADD_CONTACT_URL, System.currentTimeMillis(), LANG, context.getPassTicket());
        AddContactRequest request = new AddContactRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setOpcode(2);
        request.setSceneList(Collections.singletonList(33));
        request.setSceneListCount(1);
        request.setVerifyContent(content);
        request.setSkey(context.getSkey());
        VerifyUserRequest verifyUserRequest = new VerifyUserRequest();
        verifyUserRequest.setValue(username);
        request.setVerifyUserListSize(1);
        request.setVerifyUserList(Collections.singletonList(verifyUserRequest));
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "添加联系人")).build();
    }

    public static Request getWeChatRequest(WeChatContext context, String url, String tag) {
        return requestBuilderForWeChat(context).url(context.getDomain() + url).tag(tag(context, tag)).build();

    }

    public static Request statusReportRequests(WeChatContext context, String username, Integer code) {
        String url = context.getDomain() + URLUtils.format(WeChatConstants.WX_STATUS_REPORT_URL, LANG, context.getPassTicket());
        StatusReportRequest request = new StatusReportRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setClientMsgId(WeChatUtil.generateClientMediaId());
        request.setCode(code);
        request.setFromUserName(context.getUser().getUserName());
        request.setToUserName(username);
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "状态通知")).build();
    }

    public static Request statReportRequest(WeChatContext context, List<StatReportRequest.Info> infos) {
        String url = context.getDomain() + URLUtils.format(WeChatConstants.WX_STAT_REPORT, context.getPassTicket());
        StatReportRequest request = new StatReportRequest();
        request.setBaseRequest(context.getBaseRequest());
        request.setList(infos);
        return requestBuilderForWeChat(context).url(url).post(generateJSONBody(request)).tag(tag(context, "模拟微信statReport")).build();
    }
}
