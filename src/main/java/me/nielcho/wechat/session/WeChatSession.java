package me.nielcho.wechat.session;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.handler.DelContactHandler;
import me.nielcho.wechat.handler.MessageHandler;
import me.nielcho.wechat.handler.ModContactHandler;
import me.nielcho.wechat.predicate.ContactPredicate;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.request.BaseRequest;
import me.nielcho.wechat.response.*;
import me.nielcho.wechat.util.OkHttp;
import me.nielcho.wechat.util.WeChatRequests;
import me.nielcho.wechat.util.WeChatUtil;
import okhttp3.Request;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class WeChatSession implements Runnable {

    private static final Pattern UUID_PATTERN = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\"");
    private static final Pattern SYNC_CHECK_PATTERN = Pattern.compile("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"}");
    private static final Pattern LOGIN_CODE_PATTERN = Pattern.compile("window.code=(\\d+);");
    private static final Pattern USER_AVATAR_PATTERN = Pattern.compile("window.userAvatar = '(\\S+?)'");
    private static final Pattern LOGIN_REDIRECT_PATTERN = Pattern.compile("window.redirect_uri=\"(\\S+?)\"");
    private static final ContactPredicate CONTACT_PREDICATE_INSTANCE = new ContactPredicate();
    private final WeChatContext context;
    @Autowired
    private List<MessageHandler> messageHandlers;
    @Autowired
    private ModContactHandler modContactHandler;
    @Autowired
    private DelContactHandler delContactHandler;
    @Autowired
    private ContactRepository contactRepository;
    private EnumMap<WeChatConstants.MessageType, MessageHandler> messageHandlerMap = new EnumMap<>(WeChatConstants.MessageType.class);
    private String sessionId;
    // default to true
    private volatile boolean running = true;
    private long sleepTime = 0;
    private Consumer<Map<String, String>> cookieConsumer = (m) -> getContext().getCookies().putAll(m);

    public WeChatSession(Integer employeeId) {
        this.context = new WeChatContext();
        context.setEmployeeId(employeeId);
    }

    @PostConstruct
    public void postConstruct() {
        messageHandlers.forEach(messageHandler -> messageHandlerMap.put(messageHandler.getSupportedType(), messageHandler));
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public WeChatContext getContext() {
        return context;
    }

    private void info(String message, Object... params) {
        log.info("[*] |" + context.getEmployeeId() + "|" + context.getUuid() + "|:" + message, params);
    }

    private void warn(String message, Object... params) {
        log.warn("[*] |" + context.getEmployeeId() + "|" + context.getUuid() + "|:" + message, params);
    }

    private void error(String message, Object... params) {
        log.error("[*] |" + context.getEmployeeId() + "|" + context.getUuid() + "|:" + message, params);
    }

    // 开始登录,获取登录使用的uuid,获取uuid失败(1. http请求失败 2. 返回的数据不正确)直接关闭
    public void startLogin() {
        context.setScan(System.currentTimeMillis());
        context.setUserAgent(WeChatUtil.getRandomUserAgent());
        Map<String, String> cookieMap = context.getCookies();
        cookieMap.put("refreshTime", "5");
        cookieMap.put("MM_WX_NOTIFY_STATE", "1");
        cookieMap.put("MM_WX_SOUND_STATE", "1");
        cookieMap.put("pgv_pvi", BigDecimal.valueOf(WeChatUtil.getPGVPVI()).toPlainString() + "");
        cookieMap.put("pgv_si", "s" + BigDecimal.valueOf(WeChatUtil.getPGVPVI()).toPlainString());
        info("请求登录, context: {}", context);
        Request request = WeChatRequests.startLoginRequest(context);
        String result = OkHttp.doRequest(request, cookieConsumer);
        List<String> matched = match(UUID_PATTERN, result);
        boolean success = false;
        if (!matched.isEmpty()) {
            String code = matched.get(0);
            if ("200".equals(code)) {
                String uuid = matched.get(1);
                success = true;
                info("获取UUID成功: {}", uuid);
                context.setUuid(uuid);
                context.setTip(WeChatConstants.NOT_SCANNED);
                context.setState(WeChatConstants.CONTEXT_STATE_LOGINING);
            }
        }
        if (!success) {
            close("获取UUID失败, response: " + result);
        }
    }


    private void waitingForLogin() {
        try {
            String result = OkHttp.doRequest(WeChatRequests.waitForLoginRequest(context), cookieConsumer);
            if (null == result) {
                warn("等待登录返回null");
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
                        context.setTip(WeChatConstants.SCANNED);
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
                        close("登录扫码超时, code: " + code);
                }
            }
        } catch (Exception e) {
            error("等待登录时发生异常: {}", e);
        }
    }

    private boolean openStatusNotify() {
        Request request = WeChatRequests.openStatusNotifyRequest(context);
        BaseWxResponse response = OkHttp.doRequest(request, BaseWxResponse.class, cookieConsumer);
        return BaseWxResponse.isSuccess(response);
    }


    private void login(String redirectUrl) {
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
            if (null == responseText) {
                warn("登录重定向返回null response");
                close("登录失败");
                return;
            }
            info("登录: url:{}, response:{}", redirectUrl + "&fun=new&version=v2", responseText);
            if (responseText.contains("<ret>1203</ret>")) {
                warn("登录异常:{}", responseText);
                context.setLogoutCode(1203);
                close("登录环境异常");
                return;
            }
            Document document = DocumentHelper.parseText(responseText);
            Element root = document.getRootElement();
            context.setLoginEnd(System.currentTimeMillis());
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
            context.setSessionId(sessionId);
            context.setSid(wxsid);
            context.setSkey(skey);
            context.setUin(wxuin);
            initWeChat();
            initContact();
            info("开启状态通知: {}", openStatusNotify());
        } catch (Exception e) {
            error("登录重定向异常: {}", e);
        }
    }

    private void initWeChat() {
        info("初始化微信", context.getUin());
        context.setInitStart(System.currentTimeMillis());
        context.setDeviceId(WeChatUtil.generateDeviceID());
        context.getBaseRequest().setDeviceID(context.getDeviceId());
        Request request = WeChatRequests.initWeChatRequest(context);

        InitResponse wxInitResponse = OkHttp.doRequest(request, InitResponse.class, cookieConsumer);
        checkWeChatResponse(wxInitResponse, "初始化微信失败");
        context.setInitEnd(System.currentTimeMillis());
        String chatSet = wxInitResponse.getChatSet();
        List<String> userNames = new ArrayList<>(Arrays.asList(chatSet.split(",")));
        long start = System.currentTimeMillis();
        Collections.reverse(userNames);
        WeChatUserResponse user = wxInitResponse.getUser();
        context.setUser(user);
        context.setSyncKey(wxInitResponse.getSyncKey());
        context.setAvatar("");
        context.setState(WeChatConstants.CONTEXT_STATE_LOGINED);
        info("获取最近联系人列表: {}, 耗时: {}ms", chatSet, System.currentTimeMillis() - start);

    }

    private void checkWeChatResponse(BaseWxResponse response, String message) {
        if (!BaseWxResponse.isSuccess(response)) {
            if (response != null && response.getBaseResponse() != null) {
                context.setLogoutCode(response.getBaseResponse().getRet());
            }
            close(message);
            error("BaseResponse检查失败=> baseResponse:{}, message:{}", response, message);
            throw new RuntimeException(message);
        }
    }


    private void retry(String tag, int times, Runnable runnable) {
        int i = 0;
        while (i < times) {
            try {
                runnable.run();
                return;
            } catch (Exception e) {
                i++;
                warn(tag + ": error occurred for {} try, e:{}", i, e);
            }
        }
    }

    // 初始化联系人
    private void initContact() {
        long startAt = System.currentTimeMillis();
        context.setInitContactStart(startAt);
        info("初始化联系人 => start at: {}", startAt);
        List<ContactInfo> contactsInfo = new ArrayList<>();
        List<String> groupUserNames = new ArrayList<>();
        AtomicLong seq = new AtomicLong(0L);
        AtomicBoolean continueFlag = new AtomicBoolean(true);
        AtomicBoolean errorFlag = new AtomicBoolean(false);
        do {
            info("获得联系人, seq: {}", seq.get());
            retry("获取联系人", 3, () -> {
                Request request = WeChatRequests.getContactRequest(context, seq.get());
                GetMemberContactResponse getBatchContactResponse = OkHttp.doRequest(request, GetMemberContactResponse.class, cookieConsumer);
                // NPE在这里是可以接受的
                int ret = getBatchContactResponse.getBaseResponse().getRet();
                if (ret == 1102 || ret == 1100 || ret == 1101 || ret == 1205) {
                    warn("初始化联系人返回码异常:{}", ret);
                    context.setLogoutCode(ret);
                    close("初始化联系人返回码异常:" + ret);
                    errorFlag.set(true);
                    return;
                }
                List<GetContactResponse> memberList = getBatchContactResponse.getMemberList();
                memberList.stream()
                        .filter(CONTACT_PREDICATE_INSTANCE)
                        .forEach(contact -> {
                            ContactInfo contactInfo = ContactInfo.fromGetContactResponse(context, contact);
                            if (ContactPredicate.isGroupContact(contactInfo.getUsername())) {
                                groupUserNames.add(contactInfo.getUsername());
                            }
                            contactsInfo.add(contactInfo);
                        });
                continueFlag.set(memberList.size() > 0 && getBatchContactResponse.getSeq() != 0);
                seq.set(getBatchContactResponse.getSeq());
            });
        } while (continueFlag.get() && !errorFlag.get());
        if (errorFlag.get()) {
            return;
        }
        contactRepository.addContacts(context.getUin(), contactsInfo);
        long endAt = System.currentTimeMillis();
        info("加载联系人：{}", contactsInfo.stream()
                .map(contactInfo -> StringUtils.isEmpty(contactInfo.getRemarkName()) ? contactInfo.getNickname() : contactInfo.getRemarkName())
                .collect(Collectors.joining(",")));
        context.setLastRefreshTime(endAt);
        info("初始化联系人 => 联系人: {}, 群: {}, 结束于: {}, 耗时: {}", contactsInfo.size() - groupUserNames.size(), groupUserNames.size(), endAt, endAt - startAt);
    }

    private List<String> match(Pattern pattern, String input) {
        if (StringUtils.isEmpty(input)) return Collections.emptyList();
        Matcher matcher = pattern.matcher(input);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                result.add(matcher.group(i + 1));
            }
        }
        return result;
    }

    private List<String> match(Pattern pattern, String input, int maxLength) {
        return match(pattern, input).stream().filter(line -> line.length() <= maxLength).distinct().collect(Collectors.toList());
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
                info("SyncCheck => retcode={}, selector= {}", retCode, selector);
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
                        warn("SyncCheck返回: {}", selector);
                        close("你已退出登录或在别处登录!");
                        break;
                    default:
                        warn("未知的SyncCheck 返回码 {}", retCode);
                }
            }
            DateTime now = new DateTime();
        } catch (Exception e) {
            error("SyncCheck异常:{}", e);
        }
    }

    private boolean isRunning() {
        return running;
    }

    public void close() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String stackTrace = Stream.of(stackTraceElements)
                .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith("me.nielcho"))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("->"));
        info("调用关闭会话方法, stack trace: {}", stackTrace);
        running = false;
        context.setState(WeChatConstants.CONTEXT_STATE_CLOSED);
    }


    private void close(String message) {
        context.setMessage(message);
        close();
    }

    private void wxsync() {
        Request request = WeChatRequests.wxSyncRequest(context);
        SyncResponse syncResponse = OkHttp.doRequest(request, SyncResponse.class, cookieConsumer);
        checkWeChatResponse(syncResponse, "WxSync失败");
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

        info("WxSync => msgCount:{}, handled:{}, delContact:{}, modContact:{}", addMsgCount, handled, delContactCount, modContactCount);
        if (addMsgCount == 0 && modContactCount == 0 && delContactCount == 0) {
            try {
                Thread.sleep(sleepTime += 2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sleepTime = sleepTime > 10000 ? 0 : sleepTime;
        }
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
        String threadName = Thread.currentThread().getName();
        info("开启后台线程: {}, WeChatSession: {}", threadName, this.toString());
        try {
            while (isRunning()) {
                if (WeChatConstants.CONTEXT_STATE_LOGINING.equals(context.getState())) {
                    waitingForLogin();
                } else if (WeChatConstants.CONTEXT_STATE_LOGINED.equals(context.getState())) {
                    syncCheck();
                }
            }
        } catch (Exception e) {
            error("[x] |{}| error run session, e: {}", context.getUin(), e);
        } finally {
            info("[x] |{}| close session finally block", context.getUin());
            info("关闭WeChatSession => {}", context);
            info("关闭后台线程: {}", threadName);
        }

    }

}
