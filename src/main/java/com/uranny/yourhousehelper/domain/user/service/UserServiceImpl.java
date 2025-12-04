package com.uranny.yourhousehelper.domain.user.service;

import com.uranny.yourhousehelper.domain.user.dto.request.UserSignInRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.request.UserSignUpRequestDto;
import com.uranny.yourhousehelper.domain.user.dto.response.UserSignInResponseDto;
import com.uranny.yourhousehelper.domain.user.entity.User;
import com.uranny.yourhousehelper.domain.user.repository.UserRepository;
import com.uranny.yourhousehelper.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public void signUp(UserSignUpRequestDto signUpDto) {
        if (userRepository.findByUsername(signUpDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        User user = User.builder()
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSignInResponseDto signIn(UserSignInRequestDto signInDto) {
        User user = userRepository.findByUsername(signInDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.createToken(user.getUsername());
        return new UserSignInResponseDto(token);
    }
}
