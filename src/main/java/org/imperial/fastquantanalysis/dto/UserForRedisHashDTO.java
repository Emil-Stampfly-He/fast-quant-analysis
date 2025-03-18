package org.imperial.fastquantanalysis.dto;

import lombok.Data;

@Data
public class UserForRedisHashDTO {

    private String id;

    private String emailId;

    private String firstName;

    private String lastName;
}
