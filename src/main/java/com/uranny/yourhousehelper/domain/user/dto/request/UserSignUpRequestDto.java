package com.uranny.yourhousehelper.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignUpRequestDto {

    @NotBlank(message = "유저 이름은 필수입니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
