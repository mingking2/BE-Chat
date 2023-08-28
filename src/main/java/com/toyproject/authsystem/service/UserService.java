package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // newUser 객체에 있는 이메일의 중복 확인 후 db에 저장하고 해당 객체 반환
    public User register(User newUser) {
        // 이메일 중복 확인
        log.info(newUser.getEmail());
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if(existingUser != null) {
            return null;
        }

        newUser = userRepository.save(newUser);
        return newUser;
    }

    // 이메일과 비밀번호가 db와 일치하는지 확인해서 일치하면 객체 반환
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    // test를 위한 db초기화기능 넣음
    public void resetData() {
        userRepository.deleteAll();
        userRepository.resetIdSequence();
    }
}
