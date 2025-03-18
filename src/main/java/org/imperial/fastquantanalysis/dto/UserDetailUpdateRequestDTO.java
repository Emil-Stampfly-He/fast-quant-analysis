package org.imperial.fastquantanalysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDetailUpdateRequestDTO {

//    @Schema(description = "email of user", example = "hesongyi2002@gmail.com")
//    @NotBlank(message = "email-id value should not be empty")
//    @Max(value = 50, message = "email-id should not exceed more than 50 characters")
//    private String emailId;

    @Schema(description = "new first-name of user", example = "Emil") // Describe info for specified fields for Swagger
    @NotBlank(message = "first-name value should not be empty")
    @Max(value = 50, message = "first-name should not exceed more than 50 characters")
    private final String firstName;

    @Schema(description = "new last-name of user", example = "He")
    @NotBlank(message = "last-name value should not be empty")
    @Max(value = 50, message = "last-name should not be more than 50 characters")
    private final String lastName;

    @Schema(description = "new date-of-birth of user", example = "2002-08-08")
    @NotBlank(message = "date-of-birth should not be empty")
    private final LocalDate birthDate;
}
