package com.uranny.yourhousehelper.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSignInResponseDto {
    private String accessToken;
}
