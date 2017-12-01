package me.nielcho.wechat.session;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.predicate.ContactPredicate;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.request.BaseRequest;
import me.nielcho.wechat.response.*;
import me.nielcho.wechat.handler.*;
import me.nielcho.wechat.service.WeChatService;
import me.nielcho.wechat.util.OkHttp;
import me.nielcho.wechat.util.WeChatRequests;
import me.nielcho.wechat.util.WeChatUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static me.nielcho.wechat.constants.WeChatConstants.*;
import static me.nielcho.wechat.util.WeChatUtil.match;

@Slf4j
public class WeChatSession implements Runnable {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private final String id;
    private final WeChatContext context = new WeChatContext();
    private Consumer<Map<String, String>> cookieConsumer = (m) -> context.getCookies().putAll(m);
    private volatile boolean running;
    private long sleepTime;


    @Autowired(required = false)
    private ModContactHandler modContactHandler;

    @Autowired(required = false)
    private DelContactHandler delContactHandler;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private WeChatService weChatService;

    @Resource
    private List<MessageHandler> messageHandlers;

    private Map<MessageType, MessageHandler> messageHandlerMap = new HashMap<>();

    public WeChatSession(String id) {
        this.id = id;
        context.setId(id);
    }

    public WeChatContext getContext() {
        return context;
    }

    @PostConstruct
    public void postConstruct() {
        if (CollectionUtils.isNotEmpty(messageHandlers)) {
            messageHandlers.forEach(messageHandler -> messageHandlerMap.put(messageHandler.getSupportedType(), messageHandler));
        }
    }

    private void info(String message, Object... params) {
        log.info("[*] |" + id + "|" + context.getUuid() + "|:" + message, params);
    }

    private void warn(String message, Object... params) {
        log.warn("[*] |" + id + "|" + context.getUuid() + "|:" + message, params);
    }

    private void error(String message, Object... params) {
        log.error("[*] |" + id + "|" + context.getUuid() + "|:" + message, params);
    }

    public void startLogin() {
        Map<String, String> cookieMap = context.getCookies();
        cookieMap.put("refreshTime", "5");
        cookieMap.put("MM_WX_NOTIFY_STATE", "1");
        cookieMap.put("MM_WX_SOUND_STATE", "1");
        cookieMap.put("pgv_pvi", BigDecimal.valueOf(WeChatUtil.getPGVPVI()).toPlainString() + "");
        cookieMap.put("pgv_si", "s" + BigDecimal.valueOf(WeChatUtil.getPGVPVI()).toPlainString());
        Request request = WeChatRequests.startLoginRequest(context);
        String result = OkHttp.doRequest(request, cookieConsumer);
        List<String> matched = WeChatUtil.match(UUID_PATTERN, result);
        boolean success = false;
        if (!matched.isEmpty()) {
            String code = matched.get(0);
            if ("200".equals(code)) {
                String uuid = matched.get(1);
                success = true;
                context.setUuid(uuid);
                context.setTip(NOT_SCANNED);
                info("获取UUID成功");
            }
        }
        if (!success) {
            this.running = false;
            info("获取UUID失败");
        }
        context.setState(SESSION_STATE_LOGGING);
    }

    public String getUUID() {
        return context.getUuid();
    }

    private void waitingForLogin() {
        try {
            String result = OkHttp.doRequest(WeChatRequests.waitForLoginRequest(context), cookieConsumer);
            if (null == result) {
                return;
            }
            List<String> matched = match(LOGIN_CODE_PATTERN, result);
            if (CollectionUtils.isNotEmpty(matched)) {
                String code = matched.get(0);
                switch (code) {
                    case "408":
                        info("等待用户扫码");
                        break;
                    case "201":
                        info("用户已扫码");
                        context.setTip(SCANNED);
                        List<String> avatar = match(USER_AVATAR_PATTERN, result);
                        if (CollectionUtils.isNotEmpty(avatar)) {
                            context.setAvatar(avatar.get(0));
                        }
                        break;
                    case "200":
                        info("用户已确认登录");
                        List<String> redirectUrl = match(LOGIN_REDIRECT_PATTERN, result);
                        if (CollectionUtils.isNotEmpty(redirectUrl)) {
                            login(redirectUrl.get(0));
                        }
                        break;
                    default:
                        close("登录扫码超时");
                }
            }
        } catch (Exception e) {
            error("等待登录异常, e:{}", e);
        }
    }

    private void close(String message) {
        running = false;
        info(message);
    }

    private boolean openStatusNotify() {
        Request request = WeChatRequests.openStatusNotifyRequest(context);
        BaseWxResponse response = OkHttp.doRequest(request, BaseWxResponse.class, cookieConsumer);
        return BaseWxResponse.isSuccess(response);
    }


    private void login(String redirectUrl) {
        info("登录重定向: {}", redirectUrl);
        try {
            int domainIdx = redirectUrl.indexOf(":") + 3;
            String domain = redirectUrl.substring(0, redirectUrl.indexOf("/", domainIdx));
            context.setDomain(domain);
            String host = domain.substring(domain.lastIndexOf("/") + 1);
            context.setHost(host);
            context.setPushDomain(new StringBuilder(domain).insert(domainIdx, "webpush.").toString());
            context.setFileDomain(new StringBuilder(domain).insert(domainIdx, "file.").toString());
            Request request = WeChatRequests.doRedirectLoginRequest(context, redirectUrl);
            String responseText = OkHttp.doRequest(request, cookieConsumer);
            info("登录重定向返回: {}", responseText);
            if (null == responseText) {
                warn("登录重定向返回空Response");
                return;
            }
            if (responseText.contains("<ret>1203</ret>")) {
                close("登录环境异常");
                return;
            }
            Document document = DocumentHelper.parseText(responseText);
            Element root = document.getRootElement();
            String skey = root.element("skey").getStringValue();
            String wxsid = root.element("wxsid").getStringValue();
            String wxuin = root.element("wxuin").getStringValue();
            String pass_ticket = root.element("pass_ticket").getStringValue();
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setUin(Long.valueOf(wxuin));
            baseRequest.setSid(wxsid);
            baseRequest.setSkey(skey);
            context.setBaseRequest(baseRequest);
            context.setPassTicket(pass_ticket);
            context.setSid(wxsid);
            context.setSkey(skey);
            context.setUin(wxuin);
            wxinit();
            initContact();
            info("开启状态通知: {}", openStatusNotify());
        } catch (Exception e) {
            //
        }
    }

    private void wxinit() {
        info("初始化微信");
        context.setDeviceId(WeChatUtil.generateDeviceID());
        context.getBaseRequest().setDeviceID(context.getDeviceId());
        Request request = WeChatRequests.initWeChatRequest(context);
        InitResponse wxInitResponse = OkHttp.doRequest(request, InitResponse.class, cookieConsumer);
        UserResponse user = wxInitResponse.getUser();
        context.setUser(user);
        context.setSyncKey(wxInitResponse.getSyncKey());
        context.setAvatar("");
        context.setState(SESSION_STATE_LOGINED);
    }

    private void initContact() {
        long startAt = System.currentTimeMillis();
        List<ContactInfo> contactsInfo = new ArrayList<>();
        AtomicLong seq = new AtomicLong(0L);
        AtomicBoolean continueFlag = new AtomicBoolean(true);
        List<String> groupUserNames = new ArrayList<>();
        do {
            Request request = WeChatRequests.getContactRequest(context, seq.get());
            GetMemberContactResponse getBatchContactResponse = OkHttp.doRequest(request, GetMemberContactResponse.class, cookieConsumer);
            int ret = getBatchContactResponse.getBaseResponse().getRet();
            if (ret != 0) {
                return;
            }
            List<GetContactResponse> memberList = getBatchContactResponse.getMemberList();
            memberList.stream()
                    .filter(ContactPredicate.getInstance())
                    .forEach(contact -> {
                        if (ContactPredicate.isGroupContact(contact.getUserName())) {
                            groupUserNames.add(contact.getUserName());
                        }
                        contactsInfo.add(ContactInfo.fromGetContactResponse(context, contact));
                    });
            continueFlag.set(memberList.size() > 0 && getBatchContactResponse.getSeq() != 0);
            seq.set(getBatchContactResponse.getSeq());
        } while (continueFlag.get());
        contactRepository.addContacts(context.getUin(), contactsInfo);
        weChatService.getBatchContact(context, groupUserNames);
        long endAt = System.currentTimeMillis();
        info("初始化联系人耗时: {} ms", endAt - startAt);
    }


    private void syncCheck() {
        try {
            if (context.getSyncKey() == null) {
                return;
            }
            Request request = WeChatRequests.syncCheckRequest(context);
            String response = OkHttp.doRequest(request, cookieConsumer);
            if (response == null) {
                return;
            }
            List<String> matched = match(SYNC_CHECK_PATTERN, response);
            if (CollectionUtils.isNotEmpty(matched)) {
                String retCode = matched.get(0);
                String selector = matched.get(1);
                switch (retCode) {
                    case "0":
                        if (!"0".equals(selector)) {
                            wxsync();
                        }
                        break;
                    case "1100":
                    case "1101":
                    case "1102":
                    case "1205":
                        close("异常返回码:" + retCode);
                        break;
                    default:
                }
            }

        } catch (Exception e) {
            error("SyncCheck异常, e:{}", e);
        }
    }

    private void wxsync() {
        Request request = WeChatRequests.wxSyncRequest(context);
        SyncResponse syncResponse = OkHttp.doRequest(request, SyncResponse.class, cookieConsumer);
        context.setSyncKey(syncResponse.getSyncKey());
        int handled = 0;

        int addMsgCount = syncResponse.getAddMsgCount();
        if (addMsgCount > 0) {
            List<MessageResponse> weChatMessages = syncResponse.getAddMsgList();
            for (MessageResponse weChatMessage : weChatMessages) {
                handled += handle(weChatMessage);
            }
        }

        int modContactCount = syncResponse.getModContactCount();
        if (modContactCount > 0) {
            syncResponse.getModContactList().forEach(modContact -> modContactHandler.accept(context, modContact));
        }

        int delContactCount = syncResponse.getDelContactCount();
        if (delContactCount > 0) {
            syncResponse.getDelContactList().forEach(delContact -> delContactHandler.accept(context, delContact));
        }

        if (addMsgCount == 0 && modContactCount == 0 && delContactCount == 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime += 2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sleepTime = sleepTime > 10000 ? 0 : sleepTime;
        }
        info("WxSync => msgCount:{}, handled:{}, delContact:{}, modContact:{}", addMsgCount, handled, delContactCount, modContactCount);
    }

    private int handle(MessageResponse weChatMessage) {
        WeChatConstants.MessageType messageType = WeChatConstants.MessageType.getMessageType(weChatMessage.getMsgType(), weChatMessage.getSubMsgType(), weChatMessage.getAppMsgType());
        if (messageType == null) {
            warn("unknown message type for message msgType:{}, subMsgType:{}, appMsgType:{}", weChatMessage.getMsgType(), weChatMessage.getSubMsgType(), weChatMessage.getAppMsgType());
            return 0;
        }

        MessageHandler messageHandler = messageHandlerMap.get(messageType);
        if (messageHandler == null) {
            warn("no message handler for message type {}", messageType);
            return 0;
        }
        try {
            messageHandler.handle(context, weChatMessage);
        } catch (Exception e) {
            warn("error handle message:{}, e: {}", weChatMessage, e);
        }
        return 1;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            if (SESSION_STATE_LOGGING == context.getState()) {
                waitingForLogin();
            } else if (SESSION_STATE_LOGINED == context.getState()) {
                syncCheck();
            }
        }
        info("关闭WeChatSession => {}", context);

    }


}
