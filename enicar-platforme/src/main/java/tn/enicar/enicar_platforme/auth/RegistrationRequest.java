package tn.enicar.enicar_platforme.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message=" Firstname is mandatory")
    @NotBlank(message=" Firstname is mandatory")
    private String firstname;
    @NotEmpty(message=" Lastname is mandatory")
    @NotBlank(message=" Lastname is mandatory")
    private String lastname;
    @Email(message = "Email is not formatted")
    @NotEmpty(message=" Email is mandatory")
    @NotBlank(message=" Email is mandatory")
    private String email;
    @NotNull(message=" Cin is mandatory")
    @Digits(integer = 8, fraction = 0, message = "CIN must be an 8-digit number")
    private Integer cin;
    @NotEmpty(message=" Password is mandatory")
    @NotBlank(message=" Password is mandatory")
    @Size(min=8,message ="password should be 8 characters minimum")
    private String password;
}
