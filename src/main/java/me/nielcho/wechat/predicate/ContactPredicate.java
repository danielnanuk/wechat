package me.nielcho.wechat.predicate;

import me.nielcho.wechat.response.GetContactResponse;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 过滤公众号以及一些特殊账号
 */
public class ContactPredicate implements Predicate<GetContactResponse> {

    private static final List<String> ACCEPTED_SPECIAL_USERNAME = Collections.singletonList("filehelper");
    private static final String GROUP_CONTACT_PREFIX = "@@";

    public static boolean isGroupContact(String username) {
        return username != null && username.startsWith(GROUP_CONTACT_PREFIX);
    }

    @Override
    public boolean test(GetContactResponse getContactResponse) {
        String username = getContactResponse.getUserName();
        // 群
        if (isGroupContact(username)) {
            return true;
        }

        // 公众号
        int verifyFlag = getContactResponse.getVerifyFlag();
        if (username.startsWith("@")) {
            return (verifyFlag & 8) == 0;
        }

        // 特殊账号只保留文件传输助手
        return ACCEPTED_SPECIAL_USERNAME.contains(username);
    }
}
