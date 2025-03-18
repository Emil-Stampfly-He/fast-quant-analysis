package org.imperial.fastquantanalysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.imperial.fastquantanalysis.dto.UserDetailUpdateRequestDTO;
import org.imperial.fastquantanalysis.dto.UserLoginFormDTO;
import org.imperial.fastquantanalysis.entity.User;
import org.springframework.http.ResponseEntity;

/**
 * Service interface
 *
 * @author Emil S. He
 * @since 2025-03-16
 */
public interface IUserService extends IService<User> {

    ResponseEntity<?> sendCode(String emailId, HttpSession session);

    ResponseEntity<?> login(UserLoginFormDTO userLoginFormDTO, HttpSession session);

    ResponseEntity<?> updateUserDetails(UserDetailUpdateRequestDTO userDetailUpdateRequestDTO,
                                        HttpSession session, HttpServletRequest request);
}
