package com.uranny.yourhousehelper.domain.user.controller;

import com.uranny.yourhousehelper.domain.user.dto.request.UserReissueRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.request.UserSignInRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.request.UserSignUpRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.response.UserSignInResponseDto;
import com.uranny.yourhousehelper.domain.user.service.UserService;
import com.uranny.yourhousehelper.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> signUp(@RequestBody UserSignUpRequestDto signUpDto) {
        userService.signUp(signUpDto);
        return BaseResponse.of(HttpStatus.CREATED, "회원가입에 성공했습니다.");
    }

    @PostMapping("/signin")
    public ResponseEntity<BaseResponse<UserSignInResponseDto>> signIn(@RequestBody UserSignInRequestDto signInDto) {
        UserSignInResponseDto responseDto = userService.signIn(signInDto);
        return BaseResponse.of(responseDto, HttpStatus.OK, "로그인에 성공했습니다.");
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<UserSignInResponseDto>> reissue(@RequestBody UserReissueRequestDto reissueDto) {
        UserSignInResponseDto responseDto = userService.reissue(reissueDto);
        return BaseResponse.of(responseDto, HttpStatus.OK, "토큰 재발급에 성공했습니다.");
    }
}

