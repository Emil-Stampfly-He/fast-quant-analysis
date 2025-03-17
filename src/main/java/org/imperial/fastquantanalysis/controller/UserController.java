package org.imperial.fastquantanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.dto.UserDetailUpdateRequestDTO;
import org.imperial.fastquantanalysis.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Update profile details of logged-in user")
    public ResponseEntity<?> userDetailsUpdateHandler(
            @RequestBody UserDetailUpdateRequestDTO userDetailUpdateRequestDTO,
            HttpSession session
    ) {
        return userService.updateUserDetails(userDetailUpdateRequestDTO, session);
    }


}
