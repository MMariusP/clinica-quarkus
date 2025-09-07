package org.acme.data.boundry.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;

    @NotBlank
    @Size(max = 255)
    private String username;

    @NotBlank
    @Size(max = 255)
    private String password; // no hashing here per "no security yet"

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
}
