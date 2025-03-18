package org.imperial.fastquantanalysis.util;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.imperial.fastquantanalysis.dto.UserWithOnlyImportantInfoDTO;
import org.springframework.stereotype.Component;

/**
 * Get UserWithOnlyImportantInfoDTO object from context (with ThreadLocal)
 *
 * @author Emil S. He
 * @since 2025-03-18
 */
@Component
public class UserContext {

    @Resource
    private TokenParser tokenParser;

    public UserWithOnlyImportantInfoDTO getUserFromRequest(HttpServletRequest request) {
        return tokenParser.parseToken(request);
    }

    private static final ThreadLocal<UserWithOnlyImportantInfoDTO> threadLocal = new ThreadLocal<>();

    public static UserWithOnlyImportantInfoDTO getUser() {
        return threadLocal.get();
    }

    public static void setUser(UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO) {
        threadLocal.set(userWithOnlyImportantInfoDTO);
    }

    public static void removeUser() {
        threadLocal.remove();
    }

    public static void initUserFromRequest(HttpServletRequest request, TokenParser tokenParser) {
        UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO = tokenParser.parseToken(request);
        setUser(userWithOnlyImportantInfoDTO);
    }


}
