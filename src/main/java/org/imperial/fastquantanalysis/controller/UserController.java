package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.UserDetailUpdateRequestDTO;
import org.imperial.fastquantanalysis.dto.UserLoginFormDTO;
import org.imperial.fastquantanalysis.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller
 *
 * @author Emil S. He
 * @since 2025-03-16
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * Send verification code to user
     * @param emailId email ID
     * @param session session
     * @return OK or fail message
     * @postmantest passed
     */
    @PostMapping("/code")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Send code to user's email")
    public ResponseEntity<?> sendCode(@RequestParam("email_id") String emailId, HttpSession session) {
        return userService.sendCode(emailId, session);
    }

    /**
     * User log in
     * @param userLoginFormDTO user's log in detail, including email, verification code and password
     * @param session session
     * @return OK or fail message
     * @postmantest passed
     */
    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Login with email ID and verification code")
    public ResponseEntity<?> login(@RequestBody UserLoginFormDTO userLoginFormDTO, HttpSession session) {
        return userService.login(userLoginFormDTO, session);
    }

    /**
     * Update user's detail
     * @param userDetailUpdateRequestDTO userDetailUpdateRequestDTO user's detail, including name and birthday
     * @param session session session
     * @param request request
     * @return OK or fail message
     * @postmantest passed
     */
    @PutMapping("/update")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Update profile details of logged-in user")
    public ResponseEntity<?> userDetailsUpdateHandler(
            @RequestBody UserDetailUpdateRequestDTO userDetailUpdateRequestDTO,
            HttpSession session, HttpServletRequest request
    ) {
        return userService.updateUserDetails(userDetailUpdateRequestDTO, session, request);
    }


}
