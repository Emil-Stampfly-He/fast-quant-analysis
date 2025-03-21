package org.imperial.fastquantanalysis.util;

import cn.hutool.core.util.StrUtil;

/**
 * Check if email ID is valid or not
 *
 * @author Emil S. He
 * @since 2025-03-17
 */
public class RegexUtil {

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * Check if email ID is valid or not
     * @param emailId email ID waiting to be checked
     * @return true or false
     */
    public static boolean isEmailInvalid(String emailId) {
        return mismatch(emailId, EMAIL_REGEX);
    }

    private static boolean mismatch(String string, String regex) {
        if (StrUtil.isBlank(string)) {
            return true;
        }

        return !string.matches(regex);
    }
}
