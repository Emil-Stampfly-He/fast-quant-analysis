package org.imperial.fastquantanalysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.UserDetailUpdateRequestDTO;
import org.imperial.fastquantanalysis.mapper.UserMapper;
import org.imperial.fastquantanalysis.service.IUserService;
import org.imperial.fastquantanalysis.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service implementation class
 *
 * @author Emil S. He
 * @since 2025-03-16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * Update user's details
     * @param userDetailUpdateRequestDTO user detail update request DTO
     * @param session session
     * @return OK or fail message
     */
    @Override
    public ResponseEntity<?> updateUserDetails(UserDetailUpdateRequestDTO userDetailUpdateRequestDTO, HttpSession session) {
        // 1. Get the user from token


        // 2. If user does not exist, return fail message

        // 3. Set the new details for userDetailUpdateRequestDTO

        // 4. Save this user

        // 5. Return OK message
        return ResponseEntity.ok("User updated successfully");
    }
}
