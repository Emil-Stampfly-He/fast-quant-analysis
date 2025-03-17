package org.imperial.fastquantanalysis.dto;

import lombok.Data;

@Data
public class UserLoginFormDTO {

    private String emailId;

    private String code;

    private String password;
}
