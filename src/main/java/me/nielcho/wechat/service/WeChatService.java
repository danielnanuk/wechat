package me.nielcho.wechat.service;

import lombok.extern.slf4j.Slf4j;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.domain.ContactInfo;
import me.nielcho.wechat.predicate.ContactPredicate;
import me.nielcho.wechat.repository.ContactRepository;
import me.nielcho.wechat.util.OkHttp;
import me.nielcho.wechat.util.WeChatRequests;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import me.nielcho.wechat.response.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class WeChatService {

    @Autowired
    private ContactRepository contactRepository;

    /**
     * 发送文本消息
     */
    public SendMessageResponse sendTextMessage(WeChatContext context, String to, String content) {
        Request request = WeChatRequests.sendTextRequest(context, to, content);
        SendMessageResponse sendMessageResponse = OkHttp.doRequest(request, SendMessageResponse.class, null);
        return sendMessageResponse;
    }

    public SendMessageResponse sendMedia(WeChatContext context, MultipartFile file, String to) throws IOException {
        String mediaId = uploadMedia(context, file);
        if (StringUtils.isEmpty(mediaId)) {
            return null;
        }
        String mediaType = guessMediaType(file.getContentType());
        return "pic".equals(mediaType) ? sendImage(context, file, mediaId, to) : sendFile(context, file, mediaId, to);
    }


    public ContactInfo getContactInfo(WeChatContext context, String username, boolean remote) {
        ContactInfo contact = contactRepository.getContact(context.getUin(), username);
        if (contact == null) {
            List<String> userNames = new ArrayList<>(1);
            userNames.add(username);
            getBatchContact(context, userNames);
        }
        return contactRepository.getContact(context.getUin(), username);
    }


    public List<ContactInfo> getBatchContact(WeChatContext context, List<String> userNames) {
        long start = System.currentTimeMillis();
        Request request = WeChatRequests.getBatchContactRequest(context, userNames);
        GetBatchContactResponse batchContactResponse = OkHttp.doRequest(request, GetBatchContactResponse.class, null);
        if (batchContactResponse == null) {
            return Collections.emptyList();
        }
        List<ContactInfo> contacts = new ArrayList<>();
        batchContactResponse.getContactList().stream().filter(new ContactPredicate()).forEach(getContactResponse -> {
            ContactInfo contactInfo = ContactInfo.fromGetContactResponse(context, getContactResponse);
            // 标志当前用户是否在群内, 不在群内, 表示该群为僵尸群
            boolean inGroup = false;
            List<Member> memberList = getContactResponse.getMemberList();
            for (Member member : memberList) {
                if (Objects.equals(context.getUser().getUserName(), member.getUserName())) {
                    inGroup = true;
                    break;
                }
            }
            if (!inGroup && getContactResponse.getMemberCount() > 0) {
                log.warn("[x] |{}| possible zombie group:{}", context.getUin(), getContactResponse);
                return;
            }
            userNames.remove(contactInfo.getUsername());
            contacts.add(contactInfo);
        });
        if (CollectionUtils.isNotEmpty(userNames)) {
            log.warn("[x] |{}| can't get batch contact: {}", context.getUin(), userNames.toArray());
        }
        contactRepository.addContacts(context.getUin(), contacts);
        log.info("[x] " + (System.currentTimeMillis() - start) + " ms for get batch contact");
        return contacts;
    }

    public SetRemarkResponse setRemarkName(WeChatContext context, String to, String remarkName) {
        ContactInfo previous = getContactInfo(context, to, true);
        Request request = WeChatRequests.setRemarkRequest(context, to, remarkName);
        log.info("[x] |{}| 设置备注 -> to:{}, remarkName:{}", context.getId(), to, remarkName);
        SetRemarkResponse response = OkHttp.doRequest(request, SetRemarkResponse.class, null);
        log.info("[x] |{}| 设置备注返回 -> {}", context.getId(), response);
        if (BaseWxResponse.isSuccess(response)) {
            previous.setRemarkName(remarkName);
            contactRepository.addContacts(context.getUin(), Collections.singletonList(previous));
        }
        return response;
    }

    private String uploadMedia(WeChatContext context, MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String mediaType = guessMediaType(contentType);
        Request request =
                WeChatRequests.sendMediaRequest(context, file, contentType, mediaType, context.getMediaCount().incrementAndGet());
        UploadMediaResponse uploadMediaResponse = OkHttp.doRequest(request, UploadMediaResponse.class, null);
        if (uploadMediaResponse == null) {
            return null;
        }
        return uploadMediaResponse.getMediaId();
    }

    public Response getMsgImage(WeChatContext context, String msgId, String type) throws IOException {
        Request request = WeChatRequests.getImageRequest(context, msgId, type);
        return OkHttp.doGetResponse(request);
    }

    public Response getVoice(WeChatContext context, String msgId) throws IOException {
        Request request = WeChatRequests.getVoiceRequest(context, msgId);
        return OkHttp.doGetResponse(request);
    }

    public void logout(WeChatContext context) {
        log.info("[x] |{}| logout -> {}", context.getId(), context);
        Request request = WeChatRequests.logoutRequest(context);
        OkHttp.doRequest(request, null);
    }

    private void statusReport(WeChatContext context, String username) {
        Request request = WeChatRequests.statusReportRequests(context, username, WeChatConstants.STATUS_CODE_READ);
        try {
            OkHttp.doGetResponse(request);
        } catch (IOException e) {
            log.error("[*] |{}|{}| StatusReport Failed:{}", context.getId(), context.getUuid(), username);
        }
    }


    private String guessMediaType(String contentType) {
        if (!StringUtils.isEmpty(contentType)) {
            return contentType.toLowerCase().startsWith("image") ? "pic" : "doc";
        }
        return "doc";
    }

    private SendMessageResponse sendImage(WeChatContext context, MultipartFile file, String mediaId, String to) {
        return OkHttp.doRequest(WeChatRequests.sendImageRequest(context, to, mediaId), SendMessageResponse.class, null);
    }

    private SendMessageResponse sendFile(WeChatContext context, MultipartFile file, String mediaId, String to) {
        Request request = WeChatRequests.sendFileRequest(context, file, to, mediaId);
        SendMessageResponse response;
        response = OkHttp.doRequest(request, SendMessageResponse.class, null);
        return response;
    }

    public Response getIcon(WeChatContext context, String username) throws IOException {
        Request request = WeChatRequests.getIconRequest(context, username);
        return OkHttp.doGetResponse(request);
    }

    public Response getGroupIcon(WeChatContext context, String username, String chatRoomId) throws IOException {
        Request request = WeChatRequests.getGroupIconRequest(context, username, chatRoomId);
        return OkHttp.doGetResponse(request);
    }

    public Response getMedia(WeChatContext context, String sender, String mediaId, String filename) throws IOException {
        Request request = WeChatRequests.getMediaRequest(context, sender, mediaId, filename);
        return OkHttp.doGetResponse(request);
    }

    public Response getVideo(WeChatContext context, String msgId) throws IOException {
        Request request = WeChatRequests.getVideoRequest(context, msgId);
        return OkHttp.doGetResponse(request);
    }

    public Response getLocationImage(WeChatContext context, String locationUrl, String msgId) throws IOException {
        Request request = WeChatRequests.getLocationImage(context, locationUrl, msgId);
        return OkHttp.doGetResponse(request);
    }

    public BaseResponse addContact(WeChatContext context, String username, String content) {
        Request request = WeChatRequests.addContactRequest(context, username, content);
        BaseWxResponse response = OkHttp.doRequest(request, BaseWxResponse.class, null);
        return response.getBaseResponse();
    }

    public List<ContactInfo> getAllContacts(WeChatContext context) {
        return contactRepository.getAllContact(context.getUin());
    }
}
