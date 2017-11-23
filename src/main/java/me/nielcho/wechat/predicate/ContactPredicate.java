package me.nielcho.wechat.predicate;

import me.nielcho.wechat.response.GetContactResponse;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by drjr on 17-11-23.
 */
public class ContactPredicate implements Predicate<GetContactResponse> {

    private static final List<String> ACCEPTED_SPECIAL_USERNAME = Collections.singletonList("filehelper");
    private static final String GROUP_CONTACT_PREFIX = "@@";

    private static final ContactPredicate INSTANCE = new ContactPredicate();
    public static boolean isGroupContact(String username) {
        return username != null && username.startsWith(GROUP_CONTACT_PREFIX);
    }

    public static ContactPredicate getInstance() {
        return INSTANCE;
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
