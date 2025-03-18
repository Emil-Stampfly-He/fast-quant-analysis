package org.imperial.fastquantanalysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserWithOnlyImportantInfoDTO {

    @Schema(description = "id of user")
    private String id;

    @Schema(description = "email id of user")
    private String emailId;

    @Schema(description = "first name of user")
    private String firstName;

    @Schema(description = "last name of user")
    private String lastName;
}
