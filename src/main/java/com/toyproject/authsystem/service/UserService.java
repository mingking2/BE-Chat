package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<User> register(User newUser) {
        // 이메일 중복 확인
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if(existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 비밀번호 암호화
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    public ResponseEntity<String> login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok("로그인 성공!");
    }
}
