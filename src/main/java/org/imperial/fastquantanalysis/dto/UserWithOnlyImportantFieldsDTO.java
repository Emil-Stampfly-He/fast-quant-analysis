package org.imperial.fastquantanalysis.dto;

import lombok.Data;

@Data
public class UserWithOnlyImportantFieldsDTO {

    private String id;

    private String emailId;

    private String firstName;

    private String lastName;
}
