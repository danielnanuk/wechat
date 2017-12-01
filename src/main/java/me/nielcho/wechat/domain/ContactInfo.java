package me.nielcho.wechat.domain;

import lombok.Data;
import me.nielcho.wechat.constants.WeChatConstants;
import me.nielcho.wechat.context.WeChatContext;
import me.nielcho.wechat.predicate.ContactPredicate;
import me.nielcho.wechat.response.GetContactResponse;
import me.nielcho.wechat.response.Member;
import me.nielcho.wechat.response.ModContact;
import me.nielcho.wechat.util.WeChatUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class ContactInfo {
    String username;
    String aid;
    String nickname;
    String remarkName;
    String chatroomId;
    String icon;
    String pyInitial;
    String remarkPyInitial;
    List<ContactInfo> members;

    @Transient
    public boolean isGroup() {
        return ContactPredicate.isGroupContact(username);
    }

    private static void processEmptySkey(String skey, ContactInfo contactInfo) {
        String icon = contactInfo.getIcon();
        Map<String, String> queryString = WeChatUtil.getQueryString(icon);
        if (queryString.get("skey") == null) {
            contactInfo.setIcon(icon.replace("skey=", "skey=" + skey + "&"));
        }
    }

    private static void processGroup(ContactInfo contactInfo, List<Member> members) {
        if (CollectionUtils.isEmpty(members)) return;
        boolean isGroup = ContactPredicate.isGroupContact(contactInfo.getUsername());
        if (isGroup) {
            if (StringUtils.isEmpty(contactInfo.getNickname())) {
                contactInfo.setNickname(members.stream().map(Member::getNickName).collect(Collectors.joining("、")));
            }
        }
    }

    public static ContactInfo fromGetContactResponse(WeChatContext context, GetContactResponse getContactResponse) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setUsername(getContactResponse.getUserName());
        contactInfo.setNickname(getContactResponse.getNickName());
        contactInfo.setRemarkName(getContactResponse.getRemarkName());
        contactInfo.setChatroomId(getContactResponse.getEncryChatRoomId());
        contactInfo.setIcon(getContactResponse.getHeadImgUrl());
        contactInfo.setPyInitial(getContactResponse.getPYInitial());
        contactInfo.setRemarkPyInitial(getContactResponse.getRemarkPYInitial());
        if (CollectionUtils.isNotEmpty(getContactResponse.getMemberList())) {
            List<ContactInfo> members = getContactResponse.getMemberList().stream().map(member -> fromMemberResponse(context, getContactResponse, member)).collect(Collectors.toList());
            contactInfo.setMembers(members);
        }
        processEmptySkey(context.getSkey(), contactInfo);


        processGroup(contactInfo, getContactResponse.getMemberList());
        return contactInfo;
    }

    public static ContactInfo fromModContact(WeChatContext context, ModContact modContact) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setUsername(modContact.getUserName());
        contactInfo.setNickname(modContact.getNickName());
        contactInfo.setRemarkName(modContact.getRemarkName());
        contactInfo.setIcon(modContact.getHeadImgUrl());
        processEmptySkey(context.getSkey(), contactInfo);
        processGroup(contactInfo, modContact.getMemberList());
        return contactInfo;
    }

    public static ContactInfo fromMemberResponse(WeChatContext context, GetContactResponse getContactResponse, Member member) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setUsername(member.getUserName());
        contactInfo.setNickname(member.getNickName());
        contactInfo.setPyInitial(member.getPYInitial());
        contactInfo.setRemarkPyInitial(member.getRemarkPYInitial());
        contactInfo.setChatroomId(getContactResponse.getEncryChatRoomId());
        String encryChatRoomId = getContactResponse.getEncryChatRoomId();
        Map<String, Object> params = new HashMap<>();
        params.put("seq", 0);
        params.put("username", member.getUserName());
        params.put("skey", context.getSkey());
        params.put("chatroomid", encryChatRoomId);
        String icon = WeChatUtil.format(WeChatConstants.WX_GET_ICON, params);
        contactInfo.setIcon(icon);
        processGroup(contactInfo, getContactResponse.getMemberList());
        return contactInfo;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof ContactInfo)) return false;
        ContactInfo another = (ContactInfo) object;
        return Objects.equals(this.getUsername(), another.getUsername())
                && Objects.equals(this.getNickname(), another.getNickname())
                && Objects.equals(this.getRemarkName(), another.getRemarkName())
                && Objects.equals(this.getAid(), another.getAid());
    }
}
