package org.imperial.fastquantanalysis.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.UserDetailUpdateRequestDTO;
import org.imperial.fastquantanalysis.dto.UserLoginFormDTO;
import org.imperial.fastquantanalysis.dto.UserWithOnlyImportantInfoDTO;
import org.imperial.fastquantanalysis.mapper.UserMapper;
import org.imperial.fastquantanalysis.service.IUserService;
import org.imperial.fastquantanalysis.entity.User;
import org.imperial.fastquantanalysis.util.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.imperial.fastquantanalysis.constant.RedisKey.LOGIN_CODE_;
import static org.imperial.fastquantanalysis.constant.RedisKey.LOGIN_USER_;

/**
 * User service implementation class
 *
 * @author Emil S. He
 * @since 2025-03-16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisIdUtil redisIdUtil;

    @Resource
    private RandomStringGenerator randomStringGenerator;

    @Resource
    private UserContext userContext;

    /**
     * Send code to user's email
     * @param emailId user's email
     * @param session session
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> sendCode(String emailId, HttpSession session) {
        // 1. Check email ID
        if (RegexUtil.isEmailInvalid(emailId)) {
            // 2. If invalid, return fail message
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email is invalid");
        }

        // 3. If valid, generate verification code
        // verification code length: 6
        String code = RandomUtil.randomNumbers(6);

        // 4. Store verification code into Redis, TTL 24 hours
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_ + emailId, code, Duration.ofHours(24L));

        // 5. Send verification code
        log.debug("Verification code: {}", code);

        // 6. Return OK
        return ResponseEntity.status(HttpStatus.OK).body(code);
    }

    /**
     * User log in
     * @param userLoginFormDTO user's log in detail, including email, verification code and password
     * @param session session
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> login(UserLoginFormDTO userLoginFormDTO, HttpSession session) {
        // 1. Check email
        if (RegexUtil.isEmailInvalid(userLoginFormDTO.getEmailId())) {
            // 2. If invalid, return fail message
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email is invalid");
        }

        // 3. Check verification code
        // 3.1 Get verification code from Redis
        String cacheCode = Objects.requireNonNull(stringRedisTemplate.opsForValue()
                        .get(LOGIN_CODE_ + userLoginFormDTO.getEmailId())).replaceAll("\\x00\\x00", "");
        String code = userLoginFormDTO.getCode();

        // 3.2 If not the same, return fail message
        if (!code.equals(cacheCode)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Wrong verification code");
        }

        // 4. if the same, query the user by email
        // SELECT * FROM users WHERE email_id = ?
        User user = query().eq("email_id", userLoginFormDTO.getEmailId()).one();

        // 5. Check if user exists
        if (user == null) {
            // 6. Not exist, create a new user and store it
            createUserWithNewEmail(userLoginFormDTO.getEmailId());
        }

        // 7. Store user info into Redis
        // 7.1 Randomize token as for login token
        String token = UUID.randomUUID().toString(true);
        // 7.2 Transfer User to HashMap and store
        UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO = BeanUtil.copyProperties(user, UserWithOnlyImportantInfoDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userWithOnlyImportantInfoDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) ->
                                fieldValue.toString()));
        // 7.3 Store
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_ + token, userMap);
        // 7.4 Set TTL for token: 10 days
        stringRedisTemplate.expire(LOGIN_USER_ + token, Duration.ofDays(10L));

        // 8. Return OK with token
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    /**
     * Update user's details
     * @param userDetailUpdateRequestDTO user detail update request DTO
     * @param session session
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> updateUserDetails(UserDetailUpdateRequestDTO userDetailUpdateRequestDTO, HttpSession session,
                                               HttpServletRequest request) {
        // 1. Get current signed-in user
        UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO = userContext.getUserFromRequest(request);
        if (userWithOnlyImportantInfoDTO == null) {
            userWithOnlyImportantInfoDTO = (UserWithOnlyImportantInfoDTO) session.getAttribute("user");
            if (userWithOnlyImportantInfoDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User does not exist");
            }
        }
        String id = userWithOnlyImportantInfoDTO.getId();

        // 2. If user does not exist, return fail message
        // SELECT * FROM user WHERE id = ?
        User user = query().eq("id", id).one();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User not found");
        }

        // 3. Set the new details for userDetailUpdateRequestDTO
        user.setFirstName(userDetailUpdateRequestDTO.getFirstName());
        user.setLastName(userDetailUpdateRequestDTO.getLastName());
        user.setDateOfBirth(userDetailUpdateRequestDTO.getBirthDate());

        // 4. Update this user
        updateById(user);

        // 5. Return OK message
        return ResponseEntity.ok("User updated successfully");
    }

    /**
     * Get user's info
     * @return OK of fail message
     */
    @Override
    public ResponseEntity<?> me(HttpServletRequest request) {
        UserWithOnlyImportantInfoDTO userWithOnlyImportantInfoDTO = userContext.getUserFromRequest(request);
        if (userWithOnlyImportantInfoDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User does not log in");
        }

        return ResponseEntity.ok(userWithOnlyImportantInfoDTO);
    }

    private void createUserWithNewEmail(String emailId) {
        User user = new User();
        user.setId(redisIdUtil.nextId(emailId));
        user.setEmailId(emailId);
        user.setPassword("123456"); // default password
        user.setDateOfBirth(LocalDate.now()); // default date of birth
        user.setFirstName(randomStringGenerator.generateRandomString()); // default first name
        user.setLastName(randomStringGenerator.generateRandomString()); // default last name
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        save(user);
    }
}
