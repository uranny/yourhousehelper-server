package com.uranny.yourhousehelper.domain.user.service;

import com.uranny.yourhousehelper.domain.user.dto.request.UserSignInRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.request.UserSignUpRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.response.UserSignInResponseDto;

public interface UserService {
    void signUp(UserSignUpRequestDto signUpDto);
    UserSignInResponseDto signIn(UserSignInRequestDto signInDto);
}
