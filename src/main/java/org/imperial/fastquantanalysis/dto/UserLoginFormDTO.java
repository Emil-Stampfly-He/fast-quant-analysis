package org.imperial.fastquantanalysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLoginFormDTO {

    @Schema(description = "email-id of user that is about to log in")
    private String emailId;

    @Schema(description = "verification code for user that is about to log in")
    private String code;

    @Schema(description = "password for user that is about to log in")
    private String password;
}
