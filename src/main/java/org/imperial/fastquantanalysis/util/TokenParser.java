package org.imperial.fastquantanalysis.util;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.imperial.fastquantanalysis.dto.UserWithOnlyImportantInfoDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Parse token and get UserWithOnlyImportantInfoDTO
 *
 * @author Emil S. He
 * @since 2025-03-18
 */
@Component
public class TokenParser {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public UserWithOnlyImportantInfoDTO parseToken(HttpServletRequest request) {
        String token = extractToken(request);
        return getUserWithOnlyImportantInfoByToken(token);
    }

    private String extractToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private UserWithOnlyImportantInfoDTO getUserWithOnlyImportantInfoByToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }

        String key = "login:user:" + token;
        Map<Object, Object> userWithOnlyImportantInfoMap = stringRedisTemplate.opsForHash().entries(key);

        UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO = new UserWithOnlyImportantInfoDTO();
        userWithOnlyImportantInfoDTO.setId(userWithOnlyImportantInfoMap.get("id").toString());
        userWithOnlyImportantInfoDTO.setEmailId(userWithOnlyImportantInfoMap.get("emailId").toString());
        userWithOnlyImportantInfoDTO.setFirstName(userWithOnlyImportantInfoMap.get("firstName").toString());
        userWithOnlyImportantInfoDTO.setLastName(userWithOnlyImportantInfoMap.get("lastName").toString());

        return userWithOnlyImportantInfoDTO;
    }
}
