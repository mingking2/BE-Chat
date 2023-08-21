package com.toyproject.authsystem.service;

import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(User newUser) {
        // 이메일 중복 확인
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if(existingUser != null) {
            return null;
        }

        newUser = userRepository.save(newUser);
        return newUser;
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public void resetData() {
        userRepository.deleteAll();
        userRepository.resetIdSequence();
    }
}
